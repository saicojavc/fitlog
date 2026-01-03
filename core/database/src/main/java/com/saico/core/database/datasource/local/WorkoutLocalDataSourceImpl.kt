package com.saico.core.database.datasource.local

import com.saico.core.database.dao.WorkoutDao
import com.saico.core.database.entity.WorkoutEntity
import com.saico.core.domain.datasource.local.WorkoutLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutLocalDataSourceImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutLocalDataSource {
    override fun getWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.getWorkouts()

    override suspend fun insertWorkout(workout: WorkoutEntity) {
        workoutDao.insertWorkout(workout)
    }
}
