package com.saico.core.database.repository

import com.saico.core.database.datasource.local.WorkoutLocalDataSource
import com.saico.core.database.entity.WorkoutEntity
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
            entities.map { it.toModel() }
        }
    }

    override suspend fun insertWorkout(workout: Workout) {
        localDataSource.insertWorkout(workout.toEntity())
    }

    override fun getWorkoutsByDay(day: String): Flow<List<Workout>> {
        return localDataSource.getWorkoutsByDay(day).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getWorkoutsForLast7Days(): Flow<List<Workout>> {
        // Calculamos hace 7 días a medianoche
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // Obtenemos todos los registros desde hace 7 días en adelante (sin límite superior estricto)
        return localDataSource.getWorkoutsSince(cal.timeInMillis).map { entities ->
            entities.map { it.toModel() }
        }
    }

    private fun WorkoutEntity.toModel() = Workout(
        steps = steps,
        calories = calories,
        distance = distance,
        time = time,
        date = date,
        dayOfWeek = dayOfWeek
    )

    private fun Workout.toEntity() = WorkoutEntity(
        date = date,
        steps = steps,
        calories = calories,
        distance = distance,
        time = time,
        dayOfWeek = dayOfWeek
    )
}
