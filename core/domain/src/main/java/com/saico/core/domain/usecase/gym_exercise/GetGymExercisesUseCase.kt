package com.saico.core.domain.usecase.gym_exercise

import com.saico.core.domain.repository.GymExerciseRepository
import javax.inject.Inject

class GetGymExercisesUseCase @Inject constructor(
    private val gymExerciseRepository: GymExerciseRepository
) {
    operator fun invoke() = gymExerciseRepository.getGymExercises()
}
