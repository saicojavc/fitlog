package com.saico.core.network.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.model.AuthUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                Result.success(
                    AuthUser(
                        id = firebaseUser.uid,
                        email = firebaseUser.email,
                        displayName = firebaseUser.displayName,
                        photoUrl = firebaseUser.photoUrl?.toString()
                    )
                )
            } else {
                Result.failure(Exception("Error al obtener el usuario de Firebase"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): AuthUser? {
        return firebaseAuth.currentUser?.let {
            AuthUser(
                id = it.uid,
                email = it.email,
                displayName = it.displayName,
                photoUrl = it.photoUrl?.toString()
            )
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null
}
