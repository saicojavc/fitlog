package com.saico.feature.gymwork.state

data class GymWorkUiState(
    val elapsedTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val exercises: List<GymExerciseItem> = emptyList(),
    val totalCalories: Int = 0,
    val showAddExerciseDialog: Boolean = false,
    val editingExercise: GymExerciseItem? = null,
    val showSessionSavedDialog: Boolean = false
)

data class GymExerciseItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val weightLb: Double = 0.0,
    val isExpanded: Boolean = false
)
