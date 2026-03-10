package com.saico.core.database.datasource.local

import com.saico.core.database.dao.OutdoorSessionDao
import com.saico.core.database.entity.OutdoorSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OutdoorSessionLocalDataSourceImpl @Inject constructor(
    private val outdoorSessionDao: OutdoorSessionDao
) : OutdoorSessionLocalDataSource {
    override suspend fun insertSession(session: OutdoorSessionEntity) {
        outdoorSessionDao.insertSession(session)
    }

    override fun getAllSessions(): Flow<List<OutdoorSessionEntity>> {
        return outdoorSessionDao.getAllSessions()
    }

    override fun getSessionsByType(type: String): Flow<List<OutdoorSessionEntity>> {
        return outdoorSessionDao.getSessionsByType(type)
    }
}
