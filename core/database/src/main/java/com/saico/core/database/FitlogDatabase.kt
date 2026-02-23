package com.saico.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saico.core.database.dao.GymExerciseDao
import com.saico.core.database.dao.UserProfileDao
import com.saico.core.database.dao.WorkoutDao
import com.saico.core.database.dao.WorkoutSessionDao
import com.saico.core.database.entity.GymExerciseEntity
import com.saico.core.database.entity.UserProfileEntity
import com.saico.core.database.entity.WorkoutEntity
import com.saico.core.database.entity.WorkoutSessionEntity
import com.saico.core.database.util.FitlogTypeConverters

@Database(
    entities = [WorkoutEntity::class, UserProfileEntity::class, GymExerciseEntity::class, WorkoutSessionEntity::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(FitlogTypeConverters::class)
abstract class FitlogDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gymExerciseDao(): GymExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao

    companion object {

        val MIGRATION = object : Migration(5, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Añadimos la columna weightHistory a la tabla 'user'
                // Usamos '[]' como valor por defecto (JSON de lista vacía)
                db.execSQL("ALTER TABLE $USER_PROFILE_TABLE ADD COLUMN weightHistory TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}
