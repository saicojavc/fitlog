package com.saico.core.database.repository

import com.saico.core.database.datasource.local.UserProfileLocalDataSource
import com.saico.core.database.mapper.toDomain
import com.saico.core.database.mapper.toEntity
import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val localDataSource: UserProfileLocalDataSource
) : UserProfileRepository {
    override fun getUserProfile(): Flow<UserProfile?> {
        return localDataSource.getUserProfile().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertUserProfile(userProfile: UserProfile) {
        localDataSource.insertUserProfile(userProfile.toEntity())
    }
}
