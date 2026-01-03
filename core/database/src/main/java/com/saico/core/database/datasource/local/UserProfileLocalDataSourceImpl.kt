package com.saico.core.database.datasource.local

import com.saico.core.database.dao.UserProfileDao
import com.saico.core.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileLocalDataSourceImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileLocalDataSource {
    override fun getUserProfile(): Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    override suspend fun insertUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.insertUserProfile(userProfile)
    }
}
