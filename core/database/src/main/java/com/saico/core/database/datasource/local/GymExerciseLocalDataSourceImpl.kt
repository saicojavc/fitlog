package com.saico.core.database.datasource.local

import com.saico.core.database.dao.GymExerciseDao
import com.saico.core.database.entity.GymExerciseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GymExerciseLocalDataSourceImpl @Inject constructor(
    private val gymExerciseDao: GymExerciseDao
) : GymExerciseLocalDataSource {
    override fun getGymExercises(): Flow<List<GymExerciseEntity>> = gymExerciseDao.getGymExercises()

    override suspend fun insertGymExercise(gymExercise: GymExerciseEntity) {
        gymExerciseDao.insertGymExercise(gymExercise)
    }

    override fun getGymExercisesByDay(day: String): Flow<List<GymExerciseEntity>> {
        return gymExerciseDao.getGymExercisesByDay(day)
    }
}
