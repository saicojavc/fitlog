package com.saico.core.domain.usecase.user_profile

import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.model.UserProfile
import javax.inject.Inject

class InsertUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile) {
        userProfileRepository.insertUserProfile(userProfile)
    }
}
