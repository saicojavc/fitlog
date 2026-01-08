package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.WorkoutSessionRepository
import com.saico.core.model.WorkoutSession
import javax.inject.Inject

class InsertWorkoutSessionUseCase @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(workoutSession: WorkoutSession) {
        workoutSessionRepository.insertWorkoutSession(workoutSession)
    }
}
