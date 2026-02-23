package com.saico.core.domain.usecase.user_profile

import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.model.UserProfile
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(userProfile: UserProfile) {
        // 1. Guardar localmente siempre (Offline-First)
        repository.saveUserProfile(userProfile)
        
        // 2. Sincronizar el perfil completo con Firebase si el usuario está logueado
        // Esto asegura que cambios de peso en cualquier pantalla se suban a la nube en vivo.
        authRepository.getCurrentUser()?.let { user ->
            syncRepository.syncUserProfile(user.id, userProfile)
        }
    }
}
