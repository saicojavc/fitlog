package com.saico.feature.onboarding.state

import com.saico.core.model.UnitsConfig

data class OnboardingUiState(
    val age: String = "",
    val weight: String = "",
    val height: String = "", // Usado para CM en sistema métrico
    val heightFt: String = "", // Usado para pies en sistema imperial
    val heightIn: String = "", // Usado para pulgadas en sistema imperial
    val gender: String = "",
    val unitsConfig: UnitsConfig = UnitsConfig.METRIC,
    val isGenderMenuExpanded: Boolean = false,
    val dailySteps: Int = 6000,
    val caloriesToBurn: Int = 500,
    val isProfileConfigurationValid: Boolean = false
)
