package com.saico.core.database.datasource.local

import com.saico.core.database.entity.OutdoorSessionEntity
import kotlinx.coroutines.flow.Flow

interface OutdoorSessionLocalDataSource {
    suspend fun insertSession(session: OutdoorSessionEntity)
    fun getAllSessions(): Flow<List<OutdoorSessionEntity>>
    fun getSessionsByType(type: String): Flow<List<OutdoorSessionEntity>>
}
