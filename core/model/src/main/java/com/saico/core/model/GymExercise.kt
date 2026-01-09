package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GymExercise(
    val id: Int = 0,
    val exercises: List<GymExerciseItem>,
    val elapsedTime: Long,
    val totalCalories: Int,
    val date: Long,
    val dayOfWeek: String
)

@Parcelize
data class GymExerciseItem(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double
) : Parcelable
