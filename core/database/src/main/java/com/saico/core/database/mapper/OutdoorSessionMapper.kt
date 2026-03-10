package com.saico.core.database.mapper

import com.saico.core.database.entity.OutdoorSessionEntity
import com.saico.core.model.OutdoorSession

fun OutdoorSessionEntity.toDomain(): OutdoorSession {
    return OutdoorSession(
        id = id,
        activityType = activityType,
        steps = steps,
        averageSpeed = averageSpeed,
        distance = distance,
        elevation = elevation,
        time = time,
        date = date,
        routePath = routePath
    )
}

fun OutdoorSession.toEntity(): OutdoorSessionEntity {
    return OutdoorSessionEntity(
        id = id,
        activityType = activityType,
        steps = steps,
        averageSpeed = averageSpeed,
        distance = distance,
        elevation = elevation,
        time = time,
        date = date,
        routePath = routePath
    )
}
