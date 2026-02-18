package com.saico.core.database.mapper

import com.saico.core.database.entity.UserProfileEntity
import com.saico.core.model.UserProfile

fun UserProfileEntity.toDomain() = UserProfile(
    age = age,
    weightKg = weightKg,
    heightCm = heightCm,
    gender = gender,
    dailyStepsGoal = dailyStepsGoal,
    dailyCaloriesGoal = dailyCaloriesGoal,
    level = level,
    weightHistory = weightHistory
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = 1,
    age = age,
    weightKg = weightKg,
    heightCm = heightCm,
    gender = gender,
    dailyStepsGoal = dailyStepsGoal,
    dailyCaloriesGoal = dailyCaloriesGoal,
    level = level,
    weightHistory = weightHistory
)
