package com.aiswarya.wordconnections.data.remote.api

import com.aiswarya.wordconnections.data.remote.dto.EnhancedPuzzleResponseDto
import com.aiswarya.wordconnections.data.remote.dto.HealthResponse
import com.aiswarya.wordconnections.data.remote.dto.PuzzleResponseDto
import com.aiswarya.wordconnections.data.remote.dto.ValidateGuessRequest
import com.aiswarya.wordconnections.data.remote.dto.ValidationResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PuzzleApiService {
    @GET("api/puzzle")
    suspend fun getPuzzle(@Query("seed") seed: Int? = null): PuzzleResponseDto

    @GET("api/puzzle/enhanced")
    suspend fun getEnhancedPuzzle(@Query("seed") seed: Int? = null): EnhancedPuzzleResponseDto

    @POST("api/puzzle/validate")
    suspend fun validateGuess(@Body request: ValidateGuessRequest): ValidationResponseDto

    @GET("health")
    suspend fun healthCheck(): HealthResponse
}