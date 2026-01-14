package com.saico.core.domain.repository

import com.saico.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutSessionRepository {
    suspend fun insertWorkoutSession(workoutSession: WorkoutSession)
    fun getWorkoutSessions(): Flow<List<WorkoutSession>>
}
