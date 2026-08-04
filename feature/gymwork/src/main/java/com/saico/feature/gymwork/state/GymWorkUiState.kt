package com.saico.feature.gymwork.state

import com.saico.core.model.DayRoutine
import com.saico.core.model.ExerciseSetEntry
import com.saico.core.model.WorkoutDay

data class GymWorkUiState(
    val selectedDay: WorkoutDay = WorkoutDay.MONDAY,
    val routine: DayRoutine? = null,
    val isSessionActive: Boolean = false,
    val activeExerciseIndex: Int = 0,
    val completedSetsMap: Map<Int, List<ExerciseSetEntry>> = emptyMap(),
    val restTimerSeconds: Int = 0,
    val elapsedTimeSeconds: Long = 0,
    val totalCalories: Double = 0.0,
    val isLoading: Boolean = false,
    val showSessionSavedDialog: Boolean = false
)
