package com.saico.core.domain.usecase.onboarding

import com.saico.core.domain.repository.OnboardingRepository
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        onboardingRepository.setOnboardingCompleted(completed)
    }
}
