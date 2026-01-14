package com.saico.core.domain.usecase.user_profile

import com.saico.core.domain.repository.UserProfileRepository
import com.saico.core.model.UserProfile
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile) {
        repository.saveUserProfile(userProfile)
    }
}
