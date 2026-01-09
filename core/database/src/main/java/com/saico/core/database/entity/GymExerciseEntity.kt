package com.saico.core.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.GYM_EXERCISE_TABLE
import com.saico.core.model.GymExerciseItem
import kotlinx.parcelize.Parcelize

@Entity(tableName = GYM_EXERCISE_TABLE)
@Parcelize
data class GymExerciseEntity(
   @PrimaryKey(autoGenerate = true)
   val id: Int = 0,
   val exercises: List<GymExerciseItem>,
   val elapsedTime: Long,
   val totalCalories: Int,
   val date: Long,
   val dayOfWeek: String
) : Parcelable
