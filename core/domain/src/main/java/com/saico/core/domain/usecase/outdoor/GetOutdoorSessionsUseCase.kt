package com.saico.core.domain.usecase.outdoor

import com.saico.core.domain.repository.OutdoorSessionRepository
import com.saico.core.model.OutdoorSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOutdoorSessionsUseCase @Inject constructor(
    private val repository: OutdoorSessionRepository
) {
    operator fun invoke(): Flow<List<OutdoorSession>> {
        return repository.getAllSessions()
    }
}
