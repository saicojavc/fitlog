package com.saico.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saico.core.database.dao.GymExerciseDao
import com.saico.core.database.dao.UserProfileDao
import com.saico.core.database.dao.WorkoutDao
import com.saico.core.database.entity.GymExerciseEntity
import com.saico.core.database.entity.UserProfileEntity
import com.saico.core.database.entity.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class, UserProfileEntity::class, GymExerciseEntity::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class FitlogDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gymExerciseDao(): GymExerciseDao
}
