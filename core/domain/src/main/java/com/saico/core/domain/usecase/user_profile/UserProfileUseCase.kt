package com.saico.core.domain.usecase.user_profile

import javax.inject.Inject

data class UserProfileUseCase @Inject constructor(
    val getUserProfileUseCase: GetUserProfileUseCase,
    val insertUserProfileUseCase: InsertUserProfileUseCase,
    val updateUserProfileUseCase: UpdateUserProfileUseCase
)
