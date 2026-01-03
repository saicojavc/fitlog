package com.saico.core.model

data class UserProfile(
    val age: Int,
    val weightKg: Double,
    val heightCm: Double,
    val gender: String,
    val dailyStepsGoal: Int,
    val dailyCaloriesGoal: Int,
)
