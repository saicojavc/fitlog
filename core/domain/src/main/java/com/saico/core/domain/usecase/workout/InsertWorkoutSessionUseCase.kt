package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.domain.repository.WorkoutSessionRepository
import com.saico.core.model.WorkoutSession
import javax.inject.Inject

class InsertWorkoutSessionUseCase @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(workoutSession: WorkoutSession) {
        // 1. Guardar localmente siempre (Offline-First)
        workoutSessionRepository.insertWorkoutSession(workoutSession)

        // 2. Sincronizar con Firebase si el usuario está logueado
        authRepository.getCurrentUser()?.let { user ->
            syncRepository.syncWorkoutSession(user.id, workoutSession)
        }
    }
}
