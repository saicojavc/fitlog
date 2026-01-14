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
    id = 1, // Usamos ID fijo para asegurar que siempre se actualice el mismo registro
    age = age,
    weightKg = weightKg,
    heightCm = heightCm,
    gender = gender,
    dailyStepsGoal = dailyStepsGoal,
    dailyCaloriesGoal = dailyCaloriesGoal,
)
