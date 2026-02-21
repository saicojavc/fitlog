package com.saico.core.network.usecase

import com.saico.core.domain.repository.AuthRepository
import com.saico.core.model.AuthUser
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<AuthUser> {
        return repository.loginWithGoogle(idToken)
    }
}