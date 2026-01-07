package com.saico.feature.dashboard.state

import com.saico.core.model.UserProfile
import com.saico.core.model.Workout

data class DashboardUiState(
    val userProfile: UserProfile? = null,
    val dailySteps: Int = 0,
    // Para depuración
    val totalSteps: Int = 0,
    val stepOffset: Int = 0,
    val weeklyWorkouts: List<Workout> = emptyList()
)
