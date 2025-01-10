package com.example.pants.service

import com.example.pants.data.ColorResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ColorApiService {
    @GET("id")
    suspend fun getColor(
        @Query("hsv") hsv: String
    ): ColorResponse
}
