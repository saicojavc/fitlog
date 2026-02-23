package com.saico.core.domain.repository

import com.saico.core.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithGoogle(idToken: String): Result<AuthUser>
    fun getCurrentUser(): AuthUser?
    suspend fun logout()
    val isUserLoggedIn: Boolean
    val currentUserId: Flow<String?>
}
