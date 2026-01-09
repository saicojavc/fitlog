package com.saico.core.domain.repository

import com.saico.core.model.GymExercise
import kotlinx.coroutines.flow.Flow

interface GymExerciseRepository {
    fun getGymExercises(): Flow<List<GymExercise>>
    suspend fun insertGymExercise(gymExercise: GymExercise)
    suspend fun insertGymExercises(gymExercises: List<GymExercise>)
    fun getGymExercisesByDay(day: String): Flow<List<GymExercise>>
}
