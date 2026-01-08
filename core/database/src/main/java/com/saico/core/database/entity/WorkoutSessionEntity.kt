package com.saico.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.WORKOUT_SESSION_TABLE
import java.sql.Time

@Entity(tableName = WORKOUT_SESSION_TABLE)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Int,
    val calories: Int,
    val distance: Float,
    val time: Time,
    val date: Long,
)