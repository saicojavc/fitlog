package com.saico.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saico.core.database.WORKOUT_TABLE
import com.saico.core.database.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM $WORKOUT_TABLE")
    fun getWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM $WORKOUT_TABLE WHERE dayOfWeek = :day")
    fun getWorkoutsByDay(day: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM $WORKOUT_TABLE WHERE date >= :sinceDate ORDER BY date ASC")
    fun getWorkoutsSince(sinceDate: Long): Flow<List<WorkoutEntity>>
}
