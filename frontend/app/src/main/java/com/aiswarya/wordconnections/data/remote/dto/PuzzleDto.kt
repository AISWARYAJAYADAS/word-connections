package com.aiswarya.wordconnections.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuzzleResponseDto(
    @SerialName("puzzleWords") val puzzleWords: List<String>,
    @SerialName("categories") val categories: List<String>,
    @SerialName("meta") val meta: PuzzleMetaDto
)

@Serializable
data class EnhancedPuzzleResponseDto(
    @SerialName("puzzleWords") val puzzleWords: List<String>,
    @SerialName("categories") val categories: List<String>,
    @SerialName("solution") val solution: Map<String, List<String>>,
    @SerialName("wordToCategory") val wordToCategory: Map<String, String>,
    @SerialName("groups") val groups: List<PuzzleGroupDto>,
    @SerialName("difficultyOrder") val difficultyOrder: List<String>,
    @SerialName("meta") val meta: PuzzleMetaDto
)

@Serializable
data class PuzzleGroupDto(
    @SerialName("theme") val theme: String,
    @SerialName("words") val words: List<String>,
    @SerialName("difficulty") val difficulty: String  // Changed from Int to String
)

@Serializable
data class PuzzleMetaDto(
    @SerialName("generatedAt") val generatedAt: String,
    @SerialName("totalGroups") val totalGroups: Int,
    @SerialName("puzzleId") val puzzleId: String
)

@Serializable
data class ValidateGuessRequest(
    @SerialName("puzzleId") val puzzleId: String,
    @SerialName("words") val words: List<String>
)

@Serializable
data class ValidationResponseDto(
    @SerialName("isCorrect") val isCorrect: Boolean,
    @SerialName("category") val category: String?,
    @SerialName("remainingAttempts") val remainingAttempts: Int,
    @SerialName("solvedCategories") val solvedCategories: List<String>,
    @SerialName("isOneAway") val isOneAway: Boolean
)

@Serializable
data class HealthResponse(
    @SerialName("status") val status: String,
    @SerialName("version") val version: String
)
