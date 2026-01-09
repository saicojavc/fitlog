package com.saico.core.database.mapper

import com.saico.core.database.entity.GymExerciseEntity
import com.saico.core.model.GymExercise

fun GymExerciseEntity.toDomain() = GymExercise(
    id = id,
    exercises = exercises,
    elapsedTime = elapsedTime,
    totalCalories = totalCalories,
    date = date,
    dayOfWeek = dayOfWeek
)

fun GymExercise.toEntity() = GymExerciseEntity(
    id = id,
    exercises = exercises,
    elapsedTime = elapsedTime,
    totalCalories = totalCalories,
    date = date,
    dayOfWeek = dayOfWeek
)
