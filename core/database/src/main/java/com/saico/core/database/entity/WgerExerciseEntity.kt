package com.saico.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.WGER_EXERCISE_TABLE

@Entity(tableName = WGER_EXERCISE_TABLE)
data class WgerExerciseEntity(
    @PrimaryKey val id: Int,
    val baseId: Int?,
    val name: String,
    val category: String,
    val description: String,
    val imageUrl: String?,
    val equipment: String?
)
