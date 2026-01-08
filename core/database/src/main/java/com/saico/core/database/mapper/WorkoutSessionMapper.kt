package com.saico.core.database.mapper

import com.saico.core.database.entity.WorkoutSessionEntity
import com.saico.core.model.WorkoutSession

fun WorkoutSessionEntity.toDomain() = WorkoutSession(
    id = id,
    steps = steps,
    calories = calories,
    distance = distance,
    time = time,
    date = date
)

fun WorkoutSession.toEntity() = WorkoutSessionEntity(
    id = id,
    steps = steps,
    calories = calories,
    distance = distance,
    time = time,
    date = date
)
