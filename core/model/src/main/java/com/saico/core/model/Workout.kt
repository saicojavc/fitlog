package com.saico.core.model

import java.sql.Time

data class Workout(
    val id: Int = 0,
    val steps: Int = 0,
    val calories: Int = 0,
    val distance: Double = 0.0,
    val time: Time = Time(0),
    val date: Long = 0L,
    val dayOfWeek: String = ""
)
