package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OutdoorSession(
    val id: Int = 0,
    val activityType: String = "",
    val steps: Int? = null,
    val averageSpeed: Float = 0f,
    val distance: Float = 0f,
    val elevation: Float = 0f,
    val time: Long = 0L,
    val date: Long = 0L,
    val routePath: List<LocationPoint> = emptyList()
) : Parcelable

@Parcelize
data class LocationPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable
