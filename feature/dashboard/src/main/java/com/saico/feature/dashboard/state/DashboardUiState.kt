package com.saico.feature.dashboard.state

import com.saico.core.model.AuthUser
import com.saico.core.model.GymExercise
import com.saico.core.model.UserData
import com.saico.core.model.UserProfile
import com.saico.core.model.Workout
import com.saico.core.model.WorkoutSession

data class DashboardUiState(
    val userProfile: UserProfile? = null,
    val userData: UserData? = null,
    val dailySteps: Int = 0,
    val totalSteps: Int = 0,
    val stepOffset: Int = 0,
    val weeklyWorkouts: List<Workout> = emptyList(),
    val gymExercises: List<GymExercise> = emptyList(),
    val workoutSessions: List<WorkoutSession> = emptyList(),
    val selectedFilter: HistoryFilter = HistoryFilter.TODAY,
    val remoteVersion: String? = null,
    val isLoadingLogin: Boolean = false,
    val authUser: AuthUser? = null
)

enum class HistoryFilter {
    TODAY, LAST_WEEK, LAST_MONTH, ALL
}
