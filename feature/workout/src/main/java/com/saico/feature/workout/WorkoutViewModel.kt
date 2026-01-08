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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
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

        workoutJob = viewModelScope.launch {
            // Inicia el cronómetro
            val ticker = flow { while (true) { emit(Unit); delay(1000L) } }

            combine(ticker, stepCounterSensor.steps) { _, totalStepsSinceReboot ->
                // Guarda los pasos iniciales la primera vez
                if (initialSteps == null) {
                    initialSteps = totalStepsSinceReboot
                }

                // Solo actualiza si el entrenamiento está activo
                if (_uiState.value.workoutState == WorkoutState.RUNNING) {
                    _uiState.update { currentState ->
                        val stepsTaken = (totalStepsSinceReboot - (initialSteps ?: totalStepsSinceReboot)).coerceAtLeast(0)
                        val elapsedTime = currentState.elapsedTimeInSeconds + 1
                        
                        // Obtén el perfil del usuario para los cálculos
                        val userProfile = uiState.value.userProfile // Asumiendo que se carga desde algún sitio

                        val distance = FitnessCalculator.calculateDistanceKm(
                            steps = stepsTaken,
                            heightCm = userProfile?.heightCm?.toInt() ?: 0,
                            genderString = userProfile?.gender ?: ""
                        )
                        val calories = FitnessCalculator.calculateCaloriesBurned(stepsTaken, userProfile?.weightKg ?: 0.0)
                        val averagePace = if (elapsedTime > 0) (distance / (elapsedTime / 3600.0f)) else 0.0f

                        currentState.copy(
                            elapsedTimeInSeconds = elapsedTime,
                            stepsTaken = stepsTaken,
                            distance = distance,
                            calories = calories,
                            averagePace = averagePace
                        )
                    }
                }
            }.collectLatest { /* El combine ya hace el trabajo */ }
        }
    }

    fun pauseWorkout() {
        if (_uiState.value.workoutState == WorkoutState.RUNNING) {
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
    }

    fun onDialogDismissed() {
        _uiState.value = WorkoutUiState() // Reinicia el estado
    }
}