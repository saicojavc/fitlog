package com.saico.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saico.core.database.WGER_EXERCISE_TABLE
import com.saico.core.database.entity.WgerExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WgerExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WgerExerciseEntity>)

    @Query("SELECT * FROM $WGER_EXERCISE_TABLE")
    fun getAllExercises(): Flow<List<WgerExerciseEntity>>

    @Query("SELECT COUNT(*) FROM $WGER_EXERCISE_TABLE")
    suspend fun getExerciseCount(): Int
}
