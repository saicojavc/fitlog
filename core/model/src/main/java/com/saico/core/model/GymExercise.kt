package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GymExercise(
    val id: Int = 0,
    val exercises: List<GymExerciseItem> = emptyList(),
    val elapsedTime: Long = 0L,
    val totalCalories: Int = 0,
    val date: Long = 0L,
    val dayOfWeek: String = ""
)

@Parcelize
data class GymExerciseItem(
    val id: String = "",
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val weightKg: Double = 0.0
) : Parcelable
