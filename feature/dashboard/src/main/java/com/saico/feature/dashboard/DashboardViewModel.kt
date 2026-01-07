package com.saico.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.domain.usecase.user_profile.GetUserProfileUseCase
import com.saico.core.domain.usecase.workout.GetWeeklyWorkoutsUseCase
import com.saico.core.domain.usecase.workout.InsertWorkoutUseCase
import com.saico.core.model.Workout
import com.saico.feature.dashboard.service.StepCounterSensor
import com.saico.feature.dashboard.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getWeeklyWorkoutsUseCase: GetWeeklyWorkoutsUseCase,
    private val stepCounterSensor: StepCounterSensor,
    private val stepCounterDataStore: StepCounterDataStore,
    private val insertWorkoutUseCase: InsertWorkoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getUserProfile()
        initStepCounter()
        getWeeklyWorkouts()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collectLatest {
                _uiState.update { state ->
                    state.copy(userProfile = it)
                }
            }
        }
    }

    private fun getWeeklyWorkouts() {
        viewModelScope.launch {
            getWeeklyWorkoutsUseCase().collectLatest {
                _uiState.update { state ->
                    state.copy(weeklyWorkouts = it)
                }
            }
        }
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
                    // Un nuevo día ha comenzado. Guardamos los datos de ayer.
                    savePreviousDayWorkout(offset, totalStepsSinceReboot, lastResetDate)
                    
                    // Reiniciamos el contador para el nuevo día
                    stepCounterDataStore.saveStepCounterData(totalStepsSinceReboot)
                    Triple(0, totalStepsSinceReboot, totalStepsSinceReboot) // daily, total, offset
                } else {
                    // Mismo día, solo calculamos
                    val dailySteps = (totalStepsSinceReboot - offset).coerceAtLeast(0)
                    Triple(dailySteps, totalStepsSinceReboot, offset)
                }

            }.collectLatest { (daily, total, offsetValue) ->
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
        if (yesterdaySteps <= 0) return // No guardamos si no hubo actividad

        val userProfile = _uiState.value.userProfile ?: getUserProfileUseCase().first()

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

        insertWorkoutUseCase(workout)
    }
}
