package com.saico.core.database.repository

import com.saico.core.database.datasource.local.OutdoorSessionLocalDataSource
import com.saico.core.database.mapper.toDomain
import com.saico.core.database.mapper.toEntity
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.OutdoorSessionRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.model.OutdoorSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OutdoorSessionRepositoryImpl @Inject constructor(
    private val localDataSource: OutdoorSessionLocalDataSource,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) : OutdoorSessionRepository {

    override suspend fun saveSession(session: OutdoorSession) {
        localDataSource.insertSession(session.toEntity())
        
        // Sincronizar con Firebase si el usuario está autenticado
        authRepository.getCurrentUser()?.let { user ->
            syncRepository.syncOutdoorSession(user.id, session)
        }
    }

    override fun getAllSessions(): Flow<List<OutdoorSession>> {
        return localDataSource.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSessionsByType(type: String): Flow<List<OutdoorSession>> {
        return localDataSource.getSessionsByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncWithFirebase() {
        val user = authRepository.getCurrentUser() ?: return
        val localSessions = getAllSessions().first()
        val userProfile = userProfileRepository.getUserProfile().first() ?: return
        
        // Subir todas las sesiones locales (Firebase manejará el ID/Fecha como clave)
        syncRepository.uploadAllLocalData(
            uid = user.id,
            profile = userProfile,
            workouts = emptyList(), // Dejar vacío para no sobreescribir otros datos en una sync parcial
            sessions = emptyList(),
            gymExercises = emptyList(),
            outdoorSessions = localSessions
        )
    }
}
