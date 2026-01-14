package com.saico.core.database.datasource.local

import com.saico.core.database.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

interface WorkoutSessionLocalDataSource {
    suspend fun insertWorkoutSession(workoutSession: WorkoutSessionEntity)
    fun getWorkoutSessions(): Flow<List<WorkoutSessionEntity>>
}
