package com.saico.core.domain.usecase.gym_exercise

import javax.inject.Inject

data class GymUseCase @Inject constructor(
    val getGymExercisesUseCase: GetGymExercisesUseCase,
    val insertGymExerciseUseCase: InsertGymExerciseUseCase,
    val getGymExercisesByDayUseCase: GetGymExercisesByDayUseCase
)
