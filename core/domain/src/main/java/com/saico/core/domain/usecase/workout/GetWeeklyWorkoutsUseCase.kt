package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.WorkoutRepository
import com.saico.core.model.Workout
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyWorkoutsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> {
        return workoutRepository.getWorkoutsForLast7Days()
    }
}
