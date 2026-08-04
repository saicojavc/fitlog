package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class WorkoutDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

@Parcelize
data class RoutineExercise(
    val exerciseId: Int,
    val name: String,
    val imageUrl: String?,
    val targetSets: Int,
    val targetReps: String,
    val restTimeSeconds: Int = 90
) : Parcelable

@Parcelize
data class DayRoutine(
    val dayOfWeek: WorkoutDay,
    val title: String,
    val exercises: List<RoutineExercise>
) : Parcelable

@Parcelize
data class ExerciseSetEntry(
    val setIndex: Int,
    val weightLb: Double = 0.0,
    val reps: Int = 0,
    val isCompleted: Boolean = false
) : Parcelable
