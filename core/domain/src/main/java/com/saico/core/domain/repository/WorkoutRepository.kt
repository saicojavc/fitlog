package com.saico.core.domain.repository

import com.saico.core.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getWorkouts(): Flow<List<Workout>>
    suspend fun insertWorkout(workout: Workout)
    fun getWorkoutsByDay(day: String): Flow<List<Workout>>
    fun getWorkoutsForLast7Days(): Flow<List<Workout>>
}
