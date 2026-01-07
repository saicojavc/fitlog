package com.saico.core.database.repository

import com.saico.core.database.datasource.local.WorkoutLocalDataSource
import com.saico.core.database.mapper.toDomain
import com.saico.core.database.mapper.toEntity
import com.saico.core.domain.repository.WorkoutRepository
import com.saico.core.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
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

    override fun getWorkoutsByDay(day: String): Flow<List<Workout>> {
        return localDataSource.getWorkoutsByDay(day).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWorkoutsForLast7Days(): Flow<List<Workout>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val date = calendar.timeInMillis

        return localDataSource.getWorkoutsSince(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
