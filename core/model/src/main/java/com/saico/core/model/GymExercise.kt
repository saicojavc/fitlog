package com.saico.core.model

import java.sql.Time

data class GymExercise(
    val id: Int = 0,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val time: Time,
    val calories: Int,
    val date: Long,
    val dayOfWeek: String
)
