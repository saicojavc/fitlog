package com.saico.feature.weighttracking.state

import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.model.WeightEntry

data class WeightTrackingUiState(
    val userProfile: UserProfile? = null,
    val unitsConfig: UnitsConfig = UnitsConfig.METRIC,
    val weightHistory: List<WeightEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
