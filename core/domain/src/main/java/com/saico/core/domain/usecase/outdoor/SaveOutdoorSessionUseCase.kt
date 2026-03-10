package com.saico.core.domain.usecase.outdoor

import com.saico.core.domain.repository.OutdoorSessionRepository
import com.saico.core.model.OutdoorSession
import javax.inject.Inject

class SaveOutdoorSessionUseCase @Inject constructor(
    private val repository: OutdoorSessionRepository
) {
    suspend operator fun invoke(session: OutdoorSession) {
        repository.saveSession(session)
    }
}
