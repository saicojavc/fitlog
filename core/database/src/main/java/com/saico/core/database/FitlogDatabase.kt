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

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Crear nueva tabla temporal sin 'elevation' y con 'calories'
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ${OUTDOOR_SESSION_TABLE}_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        activityType TEXT NOT NULL,
                        steps INTEGER,
                        averageSpeed REAL NOT NULL,
                        distance REAL NOT NULL,
                        calories INTEGER NOT NULL,
                        time INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        routePath TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                // 2. Copiar los datos de la tabla vieja a la nueva (poniendo calorías en 0 por defecto)
                db.execSQL(
                    """
                    INSERT INTO ${OUTDOOR_SESSION_TABLE}_new (id, activityType, steps, averageSpeed, distance, calories, time, date, routePath)
                    SELECT id, activityType, steps, averageSpeed, distance, 0, time, date, routePath FROM $OUTDOOR_SESSION_TABLE
                    """.trimIndent()
                )

                // 3. Eliminar la tabla vieja
                db.execSQL("DROP TABLE $OUTDOOR_SESSION_TABLE")

                // 4. Renombrar la tabla nueva
                db.execSQL("ALTER TABLE ${OUTDOOR_SESSION_TABLE}_new RENAME TO $OUTDOOR_SESSION_TABLE")
            }
        }
    }
}
