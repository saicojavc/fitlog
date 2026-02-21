package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val age: Int = 0,
    val weightKg: Double = 0.0,
    val heightCm: Double = 0.0,
    val gender: String = "",
    val dailyStepsGoal: Int = 0,
    val dailyCaloriesGoal: Int = 0,
    val level: String = "Beginner",
    val weightHistory: List<WeightEntry> = emptyList()
) : Parcelable
