package com.saico.feature.outdoorrun.model

import com.saico.core.model.LocationPoint
import com.saico.core.model.UnitsConfig

data class OutdoorUiState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val timeMillis: Long = 0L,
    val distanceMeters: Float = 0f,
    val steps: Int = 0,
    val currentSpeed: Float = 0f,
    val averageSpeed: Float = 0f,
    val elevationGain: Float = 0f,
    val calories: Int = 0,
    val routePath: List<LocationPoint> = emptyList(),
    val activityType: String = "outdoor_run",
    val unitsConfig: UnitsConfig = UnitsConfig.METRIC
)
