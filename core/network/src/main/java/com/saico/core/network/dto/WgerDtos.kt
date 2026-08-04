package com.saico.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WgerResponse<T>(
    @Json(name = "results") val results: List<T>
)

@JsonClass(generateAdapter = true)
data class WgerExerciseInfoDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "category") val category: WgerCategoryDto? = null,
    @Json(name = "images") val images: List<WgerImageInfoDto>? = null
)

@JsonClass(generateAdapter = true)
data class WgerImageInfoDto(
    @Json(name = "id") val id: Int,
    @Json(name = "image") val image: String,
    @Json(name = "is_main") val isMain: Boolean = false
)

@JsonClass(generateAdapter = true)
data class WgerImageDto(
    @Json(name = "id") val id: Int,
    @Json(name = "exercise") val exercise: Int? = null,
    @Json(name = "image") val image: String
)

@JsonClass(generateAdapter = true)
data class WgerCategoryDto(
    @Json(name = "id") val id: Int, @Json(name = "name") val name: String
)
