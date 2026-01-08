package com.saico.core.model

import java.sql.Time

data class WorkoutSession(
    val id: Int = 0,
    val steps: Int,
    val calories: Int,
    val distance: Float,
    val time: Time,
    val date: Long
)
