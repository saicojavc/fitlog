package com.saico.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saico.core.database.GYM_EXERCISE_TABLE
import com.saico.core.database.entity.GymExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GymExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGymExercise(gymExercise: GymExerciseEntity)

    @Query("SELECT * FROM $GYM_EXERCISE_TABLE")
    fun getGymExercises(): Flow<List<GymExerciseEntity>>

    @Query("SELECT * FROM $GYM_EXERCISE_TABLE WHERE dayOfWeek = :day")
    fun getGymExercisesByDay(day: String): Flow<List<GymExerciseEntity>>
}
