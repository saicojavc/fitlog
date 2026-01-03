package com.saico.core.domain.data.repository


import com.saico.core.database.mapper.toDomain
import com.saico.core.database.mapper.toEntity
import com.saico.core.domain.datasource.local.WorkoutLocalDataSource
import com.saico.core.domain.repository.WorkoutRepository
import com.saico.core.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutLocalDataSource
) : WorkoutRepository {
    override fun getWorkouts(): Flow<List<Workout>> {
        return localDataSource.getWorkouts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertWorkout(workout: Workout) {
        localDataSource.insertWorkout(workout.toEntity())
    }
}
