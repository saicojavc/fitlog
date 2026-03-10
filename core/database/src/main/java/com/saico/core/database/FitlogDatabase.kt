package com.saico.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saico.core.database.dao.GymExerciseDao
import com.saico.core.database.dao.OutdoorSessionDao
import com.saico.core.database.dao.UserProfileDao
import com.saico.core.database.dao.WorkoutDao
import com.saico.core.database.dao.WorkoutSessionDao
import com.saico.core.database.entity.GymExerciseEntity
import com.saico.core.database.entity.OutdoorSessionEntity
import com.saico.core.database.entity.UserProfileEntity
import com.saico.core.database.entity.WorkoutEntity
import com.saico.core.database.entity.WorkoutSessionEntity
import com.saico.core.database.util.FitlogTypeConverters

@Database(
    entities = [
        WorkoutEntity::class,
        UserProfileEntity::class,
        GymExerciseEntity::class,
        WorkoutSessionEntity::class,
        OutdoorSessionEntity::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(FitlogTypeConverters::class)
abstract class FitlogDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gymExerciseDao(): GymExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun outdoorSessionDao(): OutdoorSessionDao

    companion object {
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE $USER_PROFILE_TABLE ADD COLUMN weightHistory TEXT NOT NULL DEFAULT '[]'")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_workout_date ON $WORKOUT_TABLE (date)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_workout_session_date ON $WORKOUT_SESSION_TABLE (date)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS $OUTDOOR_SESSION_TABLE (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        activityType TEXT NOT NULL,
                        steps INTEGER,
                        averageSpeed REAL NOT NULL,
                        distance REAL NOT NULL,
                        elevation REAL NOT NULL,
                        time INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        routePath TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
