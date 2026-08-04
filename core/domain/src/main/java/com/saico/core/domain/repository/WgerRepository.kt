package com.saico.core.domain.repository

import com.saico.core.model.DayRoutine
import com.saico.core.model.WgerExercise
import com.saico.core.model.WorkoutDay
import kotlinx.coroutines.flow.Flow

interface WgerRepository {
    fun getExercises(): Flow<List<WgerExercise>>
    suspend fun preloadExercises()
    suspend fun getRoutineForDay(day: WorkoutDay): DayRoutine
    suspend fun saveCustomRoutine(routine: DayRoutine)
    suspend fun getCustomRoutine(day: WorkoutDay): DayRoutine?
    suspend fun deleteCustomRoutine(day: WorkoutDay)
}
