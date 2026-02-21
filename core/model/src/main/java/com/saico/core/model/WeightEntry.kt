package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeightEntry(
    val date: Long = 0L,
    val weight: Double = 0.0
) : Parcelable
