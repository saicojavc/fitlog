package com.saico.core.domain.repository

import com.saico.core.model.AuthUser

interface AuthRepository {
    suspend fun loginWithGoogle(idToken: String): Result<AuthUser>
    fun getCurrentUser(): AuthUser?
    suspend fun logout()
    val isUserLoggedIn: Boolean
}
