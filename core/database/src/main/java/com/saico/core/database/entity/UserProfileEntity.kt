package com.saico.core.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.USER_PROFILE_TABLE
import com.saico.core.model.WeightEntry
import kotlinx.parcelize.Parcelize

@Entity(tableName = USER_PROFILE_TABLE)
@Parcelize
class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val age: Int,
    val weightKg: Double,
    val heightCm: Double,
    val gender: String,
    val dailyStepsGoal: Int = 10000,
    val dailyCaloriesGoal: Int = 500,
    val level: String = "Beginner",
    val weightHistory: List<WeightEntry> = emptyList()
) : Parcelable
