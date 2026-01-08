package com.saico.feature.workout.state

import com.saico.core.model.UserProfile
import com.saico.feature.workout.WorkoutState

data class WorkoutUiState(
    val userProfile: UserProfile? = null,
    val workoutState: WorkoutState = WorkoutState.IDLE,
    val elapsedTimeInSeconds: Long = 0L,
    val stepsTaken: Int = 0,
    val distance: Float = 0.0f,
    val calories: Int = 0,
    val averagePace: Float = 0.0f,
    val showWorkoutSavedDialog: Boolean = false
)
