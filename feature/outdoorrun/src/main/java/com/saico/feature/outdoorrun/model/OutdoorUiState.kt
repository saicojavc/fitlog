package com.saico.feature.outdoorrun.model

import com.saico.core.model.LocationPoint

data class OutdoorUiState(
    val isRunning: Boolean = false,
    val timeMillis: Long = 0L,
    val distanceMeters: Float = 0f,
    val steps: Int = 0,
    val currentSpeed: Float = 0f,
    val averageSpeed: Float = 0f,
    val elevationGain: Float = 0f,
    val routePath: List<LocationPoint> = emptyList(),
    val activityType: String = "outdoor_run"
)