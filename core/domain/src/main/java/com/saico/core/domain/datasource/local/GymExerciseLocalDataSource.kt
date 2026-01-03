package com.saico.core.domain.datasource.local

import com.saico.core.database.entity.GymExerciseEntity
import kotlinx.coroutines.flow.Flow

interface GymExerciseLocalDataSource {
    fun getGymExercises(): Flow<List<GymExerciseEntity>>
    suspend fun insertGymExercise(gymExercise: GymExerciseEntity)
}
