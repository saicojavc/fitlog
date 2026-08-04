package com.saico.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.CUSTOM_ROUTINE_TABLE
import com.saico.core.model.RoutineExercise

@Entity(tableName = CUSTOM_ROUTINE_TABLE)
data class CustomRoutineEntity(
    @PrimaryKey val dayOfWeek: String, // WorkoutDay.name
    val title: String,
    val exercises: List<RoutineExercise>
)
