package com.saico.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saico.core.database.CUSTOM_ROUTINE_TABLE
import com.saico.core.database.entity.CustomRoutineEntity

@Dao
interface CustomRoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: CustomRoutineEntity)

    @Query("SELECT * FROM $CUSTOM_ROUTINE_TABLE WHERE dayOfWeek = :day")
    suspend fun getRoutineForDay(day: String): CustomRoutineEntity?

    @Query("DELETE FROM $CUSTOM_ROUTINE_TABLE WHERE dayOfWeek = :day")
    suspend fun deleteRoutineForDay(day: String)
}
