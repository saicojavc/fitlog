package com.saico.core.domain.datasource.local

import com.saico.core.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalDataSource {
    fun getUserProfile(): Flow<UserProfileEntity?>
    suspend fun insertUserProfile(userProfile: UserProfileEntity)
}
