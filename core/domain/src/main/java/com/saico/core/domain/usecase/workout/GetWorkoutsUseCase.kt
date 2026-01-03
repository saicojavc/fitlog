package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke() = workoutRepository.getWorkouts()
}
