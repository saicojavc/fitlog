package com.saico.feature.dashboard

import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.model.Workout
import com.saico.core.common.util.StepCounterSensor
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.usecase.gym_exercise.GymUseCase
import com.saico.core.domain.usecase.workout.WorkoutUseCase
import com.saico.core.model.UserProfile
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WorkoutSession
import com.saico.core.notification.NotificationHelper
import com.saico.feature.dashboard.state.DashboardUiState
import com.saico.feature.dashboard.state.HistoryFilter
import com.saico.feature.dashboard.util.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase,
    private val workoutUseCase: WorkoutUseCase,
    private val gymUseCase: GymUseCase,
    private val stepCounterSensor: StepCounterSensor,
    private val stepCounterDataStore: StepCounterDataStore,
    private val userDataStore: UserSettingsDataStore,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getUserProfile()
        initStepCounter()
        getWeeklyWorkouts()
        getHistoryData()
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            userDataStore.userData.collectLatest { data ->
                _uiState.update { it.copy(userData = data) }
            }
        }
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            userProfileUseCase.getUserProfileUseCase().collectLatest {
                _uiState.update { state ->
                    state.copy(userProfile = it)
                }
            }
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            userProfileUseCase.updateUserProfileUseCase(userProfile)
        }
    }

    private fun getWeeklyWorkouts() {
        viewModelScope.launch {
            workoutUseCase.getWeeklyWorkoutsUseCase().collectLatest {
                _uiState.update { state ->
                    state.copy(weeklyWorkouts = it)
                }
            }
        }
    }

    private fun getHistoryData() {
        viewModelScope.launch {
            combine(
                gymUseCase.getGymExercisesUseCase(),
                workoutUseCase.getWorkoutSessionsUseCase()
            ) { gym, sessions ->
                Pair(gym, sessions)
            }.collectLatest { (gym, sessions) ->
                _uiState.update { state ->
                    state.copy(
                        gymExercises = gym,
                        workoutSessions = sessions
                    )
                }
            }
        }
    }

    fun onFilterSelected(filter: HistoryFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun exportHistoryToPdf(context: Context) {
        val state = _uiState.value
        val filter = state.selectedFilter
        
        val filteredGym = filterData(state.gymExercises, filter) { it.date }
        val filteredSessions = filterData(state.workoutSessions, filter) { it.date }

        if (filteredGym.isEmpty() && filteredSessions.isEmpty()) {
            android.widget.Toast.makeText(context, "No hay datos para exportar", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val totalCalories = filteredGym.sumOf { it.totalCalories } + filteredSessions.sumOf { it.calories }
        val totalSteps = filteredSessions.sumOf { it.steps }
        val totalDistance = filteredSessions.sumOf { it.distance.toDouble() }
        val totalTimeSeconds = filteredGym.sumOf { it.elapsedTime } + filteredSessions.sumOf { it.time.time / 1000 }
        
        val filterName = when (filter) {
            HistoryFilter.TODAY -> "Hoy"
            HistoryFilter.LAST_WEEK -> "Última Semana"
            HistoryFilter.LAST_MONTH -> "Último Mes"
            HistoryFilter.ALL -> "Todo el Historial"
        }

        viewModelScope.launch(Dispatchers.IO) {
            PdfExporter.generateHistoryPdf(
                context = context,
                filterName = filterName,
                gymExercises = filteredGym,
                workoutSessions = filteredSessions,
                units = state.userData?.unitsConfig ?: UnitsConfig.METRIC,
                totalCalories = totalCalories,
                totalSteps = totalSteps,
                totalDistanceKm = totalDistance,
                totalTime = DateUtils.formatElapsedTime(totalTimeSeconds)
            )
        }
    }

    private fun <T> filterData(data: List<T>, filter: HistoryFilter, dateSelector: (T) -> Long): List<T> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return when (filter) {
            HistoryFilter.TODAY -> {
                data.filter { dateSelector(it) >= cal.timeInMillis }
            }
            HistoryFilter.LAST_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    cal.add(Calendar.DAY_OF_YEAR, -7)
                }
                data.filter { dateSelector(it) >= cal.timeInMillis }
            }
            HistoryFilter.LAST_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                data.filter { dateSelector(it) >= cal.timeInMillis }
            }
            HistoryFilter.ALL -> data
        }
    }

    private fun initStepCounter() {
        if (!stepCounterSensor.isSensorAvailable()) return

        viewModelScope.launch {
            combine(
                stepCounterSensor.steps,
                stepCounterDataStore.stepOffset
            ) { totalStepsSinceReboot, offset ->
                (totalStepsSinceReboot - offset).coerceAtLeast(0)
            }.collect { dailySteps ->
                _uiState.update { it.copy(dailySteps = dailySteps) }
            }
        }
    }
}
