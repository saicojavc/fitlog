package com.saico.core.database.mapper

import com.saico.core.database.entity.WorkoutEntity
import com.saico.core.model.Workout

fun WorkoutEntity.toDomain() = Workout(
    steps = steps,
    calories = calories,
    distance = distance,
    time = time,
    date = date,
    dayOfWeek = dayOfWeek
)

fun Workout.toEntity() = WorkoutEntity(
    steps = steps,
    calories = calories,
    distance = distance,
    time = time,
    date = date,
    dayOfWeek = dayOfWeek
)
