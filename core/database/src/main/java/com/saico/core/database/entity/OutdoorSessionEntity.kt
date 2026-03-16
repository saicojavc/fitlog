package com.saico.core.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saico.core.database.OUTDOOR_SESSION_TABLE
import com.saico.core.model.LocationPoint
import kotlinx.parcelize.Parcelize

@Entity(tableName = OUTDOOR_SESSION_TABLE)
@Parcelize
data class OutdoorSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityType: String,
    val steps: Int?,
    val averageSpeed: Float,
    val distance: Float,
    val calories: Int,
    val time: Long,
    val date: Long,
    val routePath: List<LocationPoint>
) : Parcelable
