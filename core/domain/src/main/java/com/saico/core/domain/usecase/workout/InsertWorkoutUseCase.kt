package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.WorkoutRepository
import com.saico.core.model.Workout
import javax.inject.Inject

class InsertWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout) {
        workoutRepository.insertWorkout(workout)
    }
}
