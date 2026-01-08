package com.saico.core.database.datasource.local

import com.saico.core.database.dao.WorkoutSessionDao
import com.saico.core.database.entity.WorkoutSessionEntity
import javax.inject.Inject

class WorkoutSessionLocalDataSourceImpl @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao
) : WorkoutSessionLocalDataSource {
    override suspend fun insertWorkoutSession(workoutSession: WorkoutSessionEntity) {
        workoutSessionDao.insertWorkoutSession(workoutSession)
    }
}
