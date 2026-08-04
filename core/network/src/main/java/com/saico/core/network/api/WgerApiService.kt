package com.saico.core.network.api

import com.saico.core.network.dto.WgerCategoryDto
import com.saico.core.network.dto.WgerExerciseDto
import com.saico.core.network.dto.WgerImageDto
import com.saico.core.network.dto.WgerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WgerApiService {
    
    @GET("exercise/?language=2")
    suspend fun getExercises(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): WgerResponse<WgerExerciseDto>

    @GET("exerciseimage/")
    suspend fun getImages(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): WgerResponse<WgerImageDto>

    @GET("exercisecategory/")
    suspend fun getCategories(): WgerResponse<WgerCategoryDto>
}
