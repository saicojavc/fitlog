package com.saico.feature.gymwork.routineconfig

import com.saico.core.model.WgerExercise
import com.saico.core.model.WorkoutDay

data class RoutineConfigUiState(
    val selectedDay: WorkoutDay = WorkoutDay.MONDAY,
    val availableExercises: List<WgerExercise> = emptyList(),
    val selectedExercises: List<WgerExercise> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = listOf(
        "Pecho", "Espalda", "Bíceps", "Tríceps", "Hombros", "Piernas", "Abdominales"
    ),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)
