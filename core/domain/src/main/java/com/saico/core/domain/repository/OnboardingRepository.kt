package com.saico.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun hasCompletedOnboarding(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}
