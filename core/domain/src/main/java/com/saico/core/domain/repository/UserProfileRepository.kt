package com.saico.core.domain.repository

import com.saico.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun insertUserProfile(userProfile: UserProfile)
}
