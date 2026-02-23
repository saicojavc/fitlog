package com.saico.core.model

import java.sql.Time

data class WorkoutSession(
    val id: Int = 0,
    val steps: Int = 0,
    val calories: Int = 0,
    val distance: Float = 0.0f,
    val time: Time = Time(0),
    val date: Long = 0L
)
