package com.saico.core.domain.data.repository


import com.saico.core.database.mapper.toDomain
import com.saico.core.database.mapper.toEntity
import com.saico.core.domain.datasource.local.GymExerciseLocalDataSource
import com.saico.core.domain.repository.GymExerciseRepository
import com.saico.core.model.GymExercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GymExerciseRepositoryImpl @Inject constructor(
    private val localDataSource: GymExerciseLocalDataSource
) : GymExerciseRepository {
    override fun getGymExercises(): Flow<List<GymExercise>> {
        return localDataSource.getGymExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertGymExercise(gymExercise: GymExercise) {
        localDataSource.insertGymExercise(gymExercise.toEntity())
    }
}
