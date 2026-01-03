package com.saico.core.database.mapper

import com.saico.core.database.entity.GymExerciseEntity
import com.saico.core.model.GymExercise

fun GymExerciseEntity.toDomain() = GymExercise(
    name = name,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    time = time,
    calories = calories,
)

fun GymExercise.toEntity() = GymExerciseEntity(
    name = name,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    time = time,
    calories = calories,
)
