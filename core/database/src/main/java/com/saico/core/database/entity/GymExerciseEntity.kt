package com.saico.core.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.GYM_EXERCISE_TABLE
import kotlinx.parcelize.Parcelize
import java.sql.Time

@Entity(tableName = GYM_EXERCISE_TABLE)
@Parcelize
class GymExerciseEntity(
   @PrimaryKey(autoGenerate = true)
   val id: Int = 0,
   val name: String,
   val sets: Int,
   val reps: Int,
   val weightKg: Double = 0.0,
   val time: Time,
   val calories: Int,
   val date: Long,
   val dayOfWeek: String
) : Parcelable