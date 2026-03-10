package com.saico.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saico.core.database.OUTDOOR_SESSION_TABLE
import com.saico.core.database.entity.OutdoorSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutdoorSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: OutdoorSessionEntity)

    @Query("SELECT * FROM $OUTDOOR_SESSION_TABLE ORDER BY date DESC")
    fun getAllSessions(): Flow<List<OutdoorSessionEntity>>

    @Query("SELECT * FROM $OUTDOOR_SESSION_TABLE WHERE activityType = :type ORDER BY date DESC")
    fun getSessionsByType(type: String): Flow<List<OutdoorSessionEntity>>
}
