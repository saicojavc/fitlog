package com.saico.core.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.saico.core.database.WORKOUT_SESSION_TABLE
import kotlinx.parcelize.Parcelize
import java.sql.Time

@Entity(
    tableName = WORKOUT_SESSION_TABLE,
    indices = [Index(value = ["date"], unique = true)]
)
@Parcelize
class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Int,
    val calories: Int,
    val distance: Float,
    val time: Time,
    val date: Long
) : Parcelable
