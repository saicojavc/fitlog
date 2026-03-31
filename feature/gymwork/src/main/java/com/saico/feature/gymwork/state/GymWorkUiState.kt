package com.saico.feature.gymwork.state

import androidx.annotation.StringRes

data class GymWorkUiState(
    val elapsedTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val hasStarted: Boolean = false,
    val exercises: List<GymExerciseItem> = emptyList(),
    val totalCalories: Int = 0,
    val showAddExerciseDialog: Boolean = false,
    val editingExercise: GymExerciseItem? = null,
    val showSessionSavedDialog: Boolean = false,
    val workoutMode: GymWorkoutMode = GymWorkoutMode.NON_GUIDED,
    val guidedExercises: List<GuidedExerciseItem> = emptyList(),
    
    // Guided Session Flow
    val currentGuidedExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val guidedSessionState: GuidedSessionState = GuidedSessionState.READY,
    val restTimeRemaining: Int = 0
)

enum class GymWorkoutMode {
    GUIDED, NON_GUIDED
}

enum class GuidedSessionState {
    READY, EXERCISING, RESTING, FINISHED
}

data class GymExerciseItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val weightLb: Double = 0.0,
    val isExpanded: Boolean = false
)

data class GuidedExerciseItem(
    @StringRes val nameRes: Int,
    val sets: String,
    val reps: String,
    @StringRes val descriptionRes: Int,
    @StringRes val categoryRes: Int,
    @StringRes val repsRes: Int? = null // Movido al final para no romper el orden
)
