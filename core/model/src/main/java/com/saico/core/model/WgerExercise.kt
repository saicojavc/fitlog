package com.saico.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WgerExercise(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val imageUrl: String? = null,
    val equipment: String? = null
) : Parcelable
