package com.saico.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saico.core.database.WORKOUT_SESSION_TABLE
import com.saico.core.database.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(workoutSessionEntity: WorkoutSessionEntity)

    @Query("SELECT * FROM $WORKOUT_SESSION_TABLE ORDER BY date DESC")
    fun getWorkoutSessions(): Flow<List<WorkoutSessionEntity>>
}
