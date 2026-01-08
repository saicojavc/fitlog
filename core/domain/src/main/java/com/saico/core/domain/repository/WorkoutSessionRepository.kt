package com.saico.core.domain.repository

import com.saico.core.model.WorkoutSession

interface WorkoutSessionRepository {

    suspend fun insertWorkoutSession(workoutSession: WorkoutSession)

}
