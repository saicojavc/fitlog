package com.saico.core.domain.usecase.gym_exercise

import com.saico.core.domain.repository.GymExerciseRepository
import javax.inject.Inject

class GetGymExercisesByDayUseCase @Inject constructor(
    private val gymExerciseRepository: GymExerciseRepository
) {
    operator fun invoke(day: String) = gymExerciseRepository.getGymExercisesByDay(day)
}
