package com.saico.core.domain.usecase.onboarding

import com.saico.core.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return onboardingRepository.hasCompletedOnboarding()
    }
}
