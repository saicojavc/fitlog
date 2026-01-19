package com.saico.feature.workout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.common.util.StepCounterSensor
import com.saico.core.common.util.UnitsConverter
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.usecase.user_profile.GetUserProfileUseCase
import com.saico.core.domain.usecase.workout.InsertWorkoutSessionUseCase
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WorkoutSession
import com.saico.core.notification.NotificationHelper
import com.saico.core.ui.R
import com.saico.feature.workout.state.WorkoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val stepCounterSensor: StepCounterSensor,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val insertWorkoutSessionUseCase: InsertWorkoutSessionUseCase,
    private val userSettingsDataStore: UserSettingsDataStore,
    private val notificationHelper: NotificationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sensorJob: Job? = null
    private var initialSteps: Int? = null
    private var accumulatedStepsBeforePause: Int = 0

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            combine(
                getUserProfileUseCase(),
                userSettingsDataStore.userData
            ) { profile, settings ->
                profile to settings
            }.collectLatest { (profile, settings) ->
                _uiState.update { state ->
                    state.copy(
                        userProfile = profile,
                        unitsConfig = settings.unitsConfig
                    )
                }
            }
        }
    }

    fun startWorkout() {
        if (_uiState.value.workoutState == WorkoutState.RUNNING) return

        _uiState.update { it.copy(workoutState = WorkoutState.RUNNING) }

        if (timerJob == null) {
            startTimer()
        }

        if (sensorJob == null) {
            startSensorObservation()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (_uiState.value.workoutState == WorkoutState.RUNNING) {
                    _uiState.update { state ->
                        val newTime = state.elapsedTimeInSeconds + 1
                        val newState = state.copy(
                            elapsedTimeInSeconds = newTime,
                            averagePace = calculateAverageSpeed(state.distance, newTime)
                        )
                        updateWorkoutNotification(newState)
                        newState
                    }
                }
            }
        }
    }

    private fun startSensorObservation() {
        sensorJob = viewModelScope.launch {
            stepCounterSensor.steps.collect { totalStepsSinceReboot ->
                if (_uiState.value.workoutState == WorkoutState.RUNNING) {
                    if (initialSteps == null) {
                        initialSteps = totalStepsSinceReboot
                    }

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
                        
                        val newState = state.copy(
                            stepsTaken = totalSteps,
                            distance = distance,
                            calories = calories,
                            averagePace = calculateAverageSpeed(distance, state.elapsedTimeInSeconds)
                        )
                        updateWorkoutNotification(newState)
                        newState
                    }
                }
            }
        }
    }

    private fun updateWorkoutNotification(state: WorkoutUiState) {
        val timeStr = formatElapsedTime(state.elapsedTimeInSeconds)
        val distStr = UnitsConverter.formatDistance(state.distance.toDouble(), state.unitsConfig)
        val speedStr = if (state.unitsConfig == UnitsConfig.IMPERIAL) {
            String.format(Locale.getDefault(), "%.2f mph", state.averagePace * 0.621371f)
        } else {
            String.format(Locale.getDefault(), "%.2f km/h", state.averagePace)
        }

        // Estructura visual con iconos (emojis) para estética y claridad
        val content = "⏱️ $timeStr   📍 $distStr\n🔥 ${state.calories} kcal   ⚡ $speedStr"
        
        notificationHelper.showNotification(
            title = context.getString(R.string.workout_ongoing_title),
            message = content,
            channelId = NotificationHelper.WORKOUT_CHANNEL_ID,
            notificationId = NotificationHelper.WORKOUT_NOTIFICATION_ID,
            isOngoing = true
        )
    }

    private fun formatElapsedTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
    }

    private fun calculateAverageSpeed(distanceKm: Float, timeSeconds: Long): Float {
        if (timeSeconds <= 0) return 0f
        val hours = timeSeconds / 3600.0f
        return distanceKm / hours
    }

    fun pauseWorkout() {
        if (_uiState.value.workoutState == WorkoutState.RUNNING) {
            accumulatedStepsBeforePause = _uiState.value.stepsTaken
            initialSteps = null
            _uiState.update { it.copy(workoutState = WorkoutState.PAUSED) }
            
            notificationHelper.showNotification(
                title = "⏸️ " + context.getString(R.string.workout_paused_title),
                message = context.getString(R.string.workout_paused_msg),
                channelId = NotificationHelper.WORKOUT_CHANNEL_ID,
                notificationId = NotificationHelper.WORKOUT_NOTIFICATION_ID,
                isOngoing = true
            )
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

        notificationHelper.cancelNotification(NotificationHelper.WORKOUT_NOTIFICATION_ID)
        timerJob?.cancel()
        timerJob = null
        sensorJob?.cancel()
        sensorJob = null
        initialSteps = null
        accumulatedStepsBeforePause = 0
    }

    fun onDialogDismissed() {
        _uiState.value = WorkoutUiState().copy(
            userProfile = _uiState.value.userProfile,
            unitsConfig = _uiState.value.unitsConfig
        )
        accumulatedStepsBeforePause = 0
        initialSteps = null
    }

    override fun onCleared() {
        super.onCleared()
        // Asegurar que la notificación se limpie si se cierra la app bruscamente o se destruye el VM
        notificationHelper.cancelNotification(NotificationHelper.WORKOUT_NOTIFICATION_ID)
    }
}
