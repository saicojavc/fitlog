package com.saico.core.domain.usecase.user_profile

import com.saico.core.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    operator fun invoke() = userProfileRepository.getUserProfile()
}
