package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeightEntry(
    val weight: Double,
    val date: Long
) : Parcelable
