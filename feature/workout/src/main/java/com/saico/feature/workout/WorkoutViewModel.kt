package com.saico.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.common.util.StepCounterSensor
import com.saico.core.domain.usecase.user_profile.GetUserProfileUseCase
import com.saico.core.domain.usecase.workout.InsertWorkoutSessionUseCase
import com.saico.core.model.WorkoutSession
import com.saico.feature.workout.state.WorkoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val stepCounterSensor: StepCounterSensor,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val insertWorkoutSessionUseCase: InsertWorkoutSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var workoutJob: Job? = null
    private var initialSteps: Int? = null
    private var accumulatedStepsBeforePause: Int = 0

    init {
        getUserProfile()
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

    fun startWorkout() {
        if (_uiState.value.workoutState == WorkoutState.RUNNING) return

        _uiState.update { it.copy(workoutState = WorkoutState.RUNNING) }

        // Cancelamos cualquier job previo por seguridad
        workoutJob?.cancel()

        workoutJob = viewModelScope.launch {
            // 1. Job independiente para el Cronómetro (Exactamente 1 segundo)
            launch {
                while (true) {
                    delay(1000L)
                    if (_uiState.value.workoutState == WorkoutState.RUNNING) {
                        _uiState.update { state ->
                            val newTime = state.elapsedTimeInSeconds + 1
                            state.copy(
                                elapsedTimeInSeconds = newTime,
                                averagePace = calculateAverageSpeed(state.distance, newTime)
                            )
                        }
                    }
                }
            }

            // 2. Job independiente para los Pasos (Reacciona al sensor)
            launch {
                stepCounterSensor.steps.collect { totalStepsSinceReboot ->
                    if (initialSteps == null) {
                        initialSteps = totalStepsSinceReboot
                    }

                    if (_uiState.value.workoutState == WorkoutState.RUNNING) {
                        val currentStepsInSession = (totalStepsSinceReboot - (initialSteps ?: totalStepsSinceReboot)).coerceAtLeast(0)
                        val totalSteps = accumulatedStepsBeforePause + currentStepsInSession

                        _uiState.update { state ->
                            val userProfile = state.userProfile
                            val distance = FitnessCalculator.calculateDistanceKm(
                                steps = totalSteps,
                                heightCm = userProfile?.heightCm?.toInt() ?: 170,
                                genderString = userProfile?.gender ?: "male"
                            )
                            val calories = FitnessCalculator.calculateCaloriesBurned(totalSteps, userProfile?.weightKg ?: 70.0)

                            state.copy(
                                stepsTaken = totalSteps,
                                distance = distance,
                                calories = calories,
                                averagePace = calculateAverageSpeed(distance, state.elapsedTimeInSeconds)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun calculateAverageSpeed(distanceKm: Float, timeSeconds: Long): Float {
        if (timeSeconds <= 0) return 0f
        val hours = timeSeconds / 3600.0f
        return distanceKm / hours
    }

    fun pauseWorkout() {
        if (_uiState.value.workoutState == WorkoutState.RUNNING) {
            // Guardamos los pasos acumulados hasta ahora para que al reanudar no se pierdan
            // ni se sumen pasos dados mientras estaba en pausa
            accumulatedStepsBeforePause = _uiState.value.stepsTaken
            initialSteps = null // Se reseteará al reanudar con el nuevo valor del sensor
            _uiState.update { it.copy(workoutState = WorkoutState.PAUSED) }
        }
    }

    fun stopWorkout() {
        val currentState = _uiState.value
        if (currentState.elapsedTimeInSeconds > 0) {
            viewModelScope.launch {
                val workoutSession = WorkoutSession(
                    steps = currentState.stepsTaken,
                    calories = currentState.calories.toInt(),
                    distance = currentState.distance,
                    time = Time(currentState.elapsedTimeInSeconds * 1000),
                    date = Date().time
                )
                insertWorkoutSessionUseCase(workoutSession)
                _uiState.update { it.copy(showWorkoutSavedDialog = true) }
            }
        }

        workoutJob?.cancel()
        workoutJob = null
        initialSteps = null
        accumulatedStepsBeforePause = 0
    }

    fun onDialogDismissed() {
        _uiState.value = WorkoutUiState()
        accumulatedStepsBeforePause = 0
        initialSteps = null
    }
}
