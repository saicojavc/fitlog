package com.saico.core.model

import java.sql.Time

data class Workout(
    val steps: Int,
    val calories: Int,
    val distance: Double,
    val time: Time,
)
