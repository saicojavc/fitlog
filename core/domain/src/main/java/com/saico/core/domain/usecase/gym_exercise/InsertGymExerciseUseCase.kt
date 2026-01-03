package com.saico.core.domain.usecase.gym_exercise

import com.saico.core.domain.repository.GymExerciseRepository
import com.saico.core.model.GymExercise
import javax.inject.Inject

class InsertGymExerciseUseCase @Inject constructor(
    private val gymExerciseRepository: GymExerciseRepository
) {
    suspend operator fun invoke(gymExercise: GymExercise) {
        gymExerciseRepository.insertGymExercise(gymExercise)
    }
}
