package com.saico.core.domain.usecase.gym_exercise

import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.GymExerciseRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.model.GymExercise
import javax.inject.Inject

class InsertGymExerciseUseCase @Inject constructor(
    private val gymExerciseRepository: GymExerciseRepository,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(gymExercise: GymExercise) {
        // 1. Guardar localmente siempre (Offline-First)
        gymExerciseRepository.insertGymExercise(gymExercise)

        // 2. Sincronizar con Firebase si el usuario está logueado
        authRepository.getCurrentUser()?.let { user ->
            syncRepository.syncGymExercise(user.id, gymExercise)
        }
    }
}
