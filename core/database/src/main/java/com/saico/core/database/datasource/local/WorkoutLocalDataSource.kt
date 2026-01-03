package com.saico.core.database.datasource.local

import com.saico.core.database.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutLocalDataSource {
    fun getWorkouts(): Flow<List<WorkoutEntity>>
    suspend fun insertWorkout(workout: WorkoutEntity)
    fun getWorkoutsByDay(day: String): Flow<List<WorkoutEntity>>
}
