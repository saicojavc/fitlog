package com.saico.core.model

import java.sql.Time

data class Workout(
    val id: Int = 0,
    val steps: Int,
    val calories: Int,
    val distance: Double,
    val time: Time,
    val date: Long,
    val dayOfWeek: String
)
