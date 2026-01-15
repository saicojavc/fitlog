package com.saico.feature.dashboard

import androidx.compose.remote.creation.first
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
import com.saico.core.notification.NotificationHelper
import com.saico.feature.dashboard.state.DashboardUiState
import com.saico.feature.dashboard.state.HistoryFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        setupNotificationObserver()
    }

    private fun setupNotificationObserver() {
        viewModelScope.launch {
            // Combinamos Pasos, Perfil y Flags de Notificación en un único flujo reactivo
            combine(
                uiState.map { it.dailySteps }.distinctUntilChanged(),
                uiState.map { it.userProfile }.distinctUntilChanged(),
                userDataStore.goalReachedShownDate,
                userDataStore.halfGoalShownDate
            ) { steps, profile, lastGoalDate, lastHalfDate ->
                if (profile != null) {
                    processProgressNotifications(steps, profile, lastGoalDate, lastHalfDate)
                }
            }.collect()
        }
    }

    private suspend fun processProgressNotifications(
        dailySteps: Int,
        profile: UserProfile,
        lastGoalDate: Long,
        lastHalfDate: Long
    ) {
        val goal = profile.dailyStepsGoal
        if (goal <= 0) return

        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Notificación 100%
//        if (dailySteps >= goal ) {
//            notificationHelper.showNotification(
//                "¡Meta cumplida! 🎉",
//                "¡Increíble! Has llegado a tus $goal pasos.",
//                NotificationHelper.PROGRESS_CHANNEL_ID,
//                2001
//            )
//            userDataStore.setGoalReachedShown(System.currentTimeMillis())
//            return
//        }

        // Notificación 50%
//        if (dailySteps >= goal / 2 && lastHalfDate < todayStart && lastGoalDate < todayStart) {
//            notificationHelper.showNotification(
//                "¡Mitad del camino! 🔥",
//                "Ya llevas $dailySteps pasos. ¡Sigue así!",
//                NotificationHelper.PROGRESS_CHANNEL_ID,
//                2002
//            )
//            userDataStore.setHalfGoalShown(System.currentTimeMillis())
//        }
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

    private fun initStepCounter() {
        if (!stepCounterSensor.isSensorAvailable()) {
            return
        }

        viewModelScope.launch {
            combine(
                stepCounterSensor.steps,
                stepCounterDataStore.stepOffset,
                stepCounterDataStore.lastResetDate
            ) { totalStepsSinceReboot, offset, lastResetDate ->

                if (stepCounterDataStore.isNewDay(lastResetDate)) {
                    savePreviousDayWorkout(offset, totalStepsSinceReboot, lastResetDate)
                    stepCounterDataStore.saveStepCounterData(totalStepsSinceReboot)
                    Triple(0, totalStepsSinceReboot, totalStepsSinceReboot)
                } else {
                    val dailySteps = (totalStepsSinceReboot - offset).coerceAtLeast(0)
                    Triple(dailySteps, totalStepsSinceReboot, offset)
                }

            }.collect { (daily, total, offsetValue) ->
                _uiState.update {
                    it.copy(
                        dailySteps = daily,
                        totalSteps = total,
                        stepOffset = offsetValue
                    )
                }
            }
        }
    }

    private suspend fun savePreviousDayWorkout(previousOffset: Int, currentSensorValue: Int, previousDayDate: Long) {
        val yesterdaySteps = currentSensorValue - previousOffset
        if (yesterdaySteps <= 0) return

        val userProfile = _uiState.value.userProfile ?: userProfileUseCase.getUserProfileUseCase().first()

        val calories = FitnessCalculator.calculateCaloriesBurned(yesterdaySteps, userProfile?.weightKg ?: 0.0)
        val distance = FitnessCalculator.calculateDistanceKm(yesterdaySteps, userProfile?.heightCm?.toInt() ?: 0, userProfile?.gender ?: "")
        val activeTime = FitnessCalculator.calculateActiveTimeMinutes(yesterdaySteps)

        val calendar = Calendar.getInstance().apply { timeInMillis = previousDayDate }
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""

        val workout = Workout(
            steps = yesterdaySteps,
            calories = calories,
            distance = distance.toDouble(),
            time = Time(activeTime * 60 * 1000L),
            date = previousDayDate,
            dayOfWeek = dayOfWeek
        )

        workoutUseCase.insertWorkoutUseCase(workout)
    }
}
