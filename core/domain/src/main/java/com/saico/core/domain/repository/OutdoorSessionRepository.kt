package com.saico.core.domain.repository

import com.saico.core.model.OutdoorSession
import kotlinx.coroutines.flow.Flow

interface OutdoorSessionRepository {
    suspend fun saveSession(session: OutdoorSession)
    fun getAllSessions(): Flow<List<OutdoorSession>>
    fun getSessionsByType(type: String): Flow<List<OutdoorSession>>
    suspend fun syncWithFirebase()
}
