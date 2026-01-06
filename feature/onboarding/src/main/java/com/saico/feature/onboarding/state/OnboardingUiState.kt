package com.saico.feature.onboarding.state

data class OnboardingUiState(
    val age: String = "",
    val weight: String = "",
    val height: String = "",
    val gender: String = "",
    val isGenderMenuExpanded: Boolean = false,
    val dailySteps: Int = 10000,
    val caloriesToBurn: Int = 500
)
