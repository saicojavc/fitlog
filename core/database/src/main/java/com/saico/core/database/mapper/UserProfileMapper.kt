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
)

fun UserProfile.toEntity() = UserProfileEntity(
    age = age,
    weightKg = weightKg,
    heightCm = heightCm,
    gender = gender,
    dailyStepsGoal = dailyStepsGoal,
    dailyCaloriesGoal = dailyCaloriesGoal,
)
