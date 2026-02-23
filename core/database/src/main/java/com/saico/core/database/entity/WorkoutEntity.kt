package com.saico.core.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.WORKOUT_TABLE
import kotlinx.parcelize.Parcelize
import java.sql.Time

@Entity(tableName = WORKOUT_TABLE)
@Parcelize
class WorkoutEntity(
    @PrimaryKey
    val date: Long, // Identificador único absoluto por día
    val steps: Int,
    val calories: Int,
    val distance: Double,
    val time: Time,
    val dayOfWeek: String
) : Parcelable
