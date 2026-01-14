package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.WorkoutSessionRepository
import com.saico.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutSessionsUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    operator fun invoke(): Flow<List<WorkoutSession>> {
        return repository.getWorkoutSessions()
    }
}
