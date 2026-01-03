package com.saico.core.domain.usecase.workout

import javax.inject.Inject


data class WorkoutUseCase @Inject constructor(
    val getWorkoutsUseCase: GetWorkoutsUseCase,
    val insertWorkoutUseCase: InsertWorkoutUseCase
)

