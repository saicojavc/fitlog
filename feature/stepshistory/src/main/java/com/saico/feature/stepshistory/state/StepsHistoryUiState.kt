package com.saico.feature.stepshistory.state

import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.model.Workout

data class StepsHistoryUiState(
    val workouts: List<Workout> = emptyList(),
    val userProfile: UserProfile? = null,
    val unitsConfig: UnitsConfig = UnitsConfig.METRIC,
    val currentSteps: Int = 0,
    val selectedFilter: StepsHistoryFilter = StepsHistoryFilter.WEEKLY,
    val isLoading: Boolean = false
)

enum class StepsHistoryFilter {
    WEEKLY, MONTHLY, YEARLY
}

data class ChartData(
    val label: String,
    val value: Float,
    val isHighlighted: Boolean = false
)
