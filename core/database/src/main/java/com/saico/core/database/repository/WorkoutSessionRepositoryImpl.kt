package com.saico.core.database.repository

import com.saico.core.database.datasource.local.WorkoutSessionLocalDataSource
import com.saico.core.database.mapper.toDomain
import com.saico.core.database.mapper.toEntity
import com.saico.core.domain.repository.WorkoutSessionRepository
import com.saico.core.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutSessionRepositoryImpl @Inject constructor(
    private val workoutSessionLocalDataSource: WorkoutSessionLocalDataSource
) : WorkoutSessionRepository {

    override suspend fun insertWorkoutSession(workoutSession: WorkoutSession) {
        workoutSessionLocalDataSource.insertWorkoutSession(workoutSession.toEntity())
    }

    override fun getWorkoutSessions(): Flow<List<WorkoutSession>> {
        return workoutSessionLocalDataSource.getWorkoutSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
