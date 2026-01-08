package com.saico.core.database.datasource.local

import com.saico.core.database.entity.WorkoutSessionEntity

interface WorkoutSessionLocalDataSource {
    suspend fun insertWorkoutSession(workoutSession: WorkoutSessionEntity)
}
