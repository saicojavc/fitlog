package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OutdoorSession(
    val id: Int = 0,
    val activityType: String, // "outdoor_run" or "cycling"
    val steps: Int?,
    val averageSpeed: Float,
    val distance: Float,
    val elevation: Float,
    val time: Long, // Duration in milliseconds
    val date: Long, // Timestamp
    val routePath: List<LocationPoint>
) : Parcelable

@Parcelize
data class LocationPoint(
    val latitude: Double,
    val longitude: Double
) : Parcelable
