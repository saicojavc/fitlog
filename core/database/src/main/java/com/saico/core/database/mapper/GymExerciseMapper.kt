package com.saico.core.database.mapper

import com.saico.core.database.entity.GymExerciseEntity
import com.saico.core.model.GymExercise

fun GymExerciseEntity.toDomain() = GymExercise(
    id = id,
    name = name,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    time = time,
    calories = calories,
    date = date,
    dayOfWeek = dayOfWeek
)

fun GymExercise.toEntity() = GymExerciseEntity(
    id = id,
    name = name,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    time = time,
    calories = calories,
    date = date,
    dayOfWeek = dayOfWeek
)
