package com.aiswarya.wordconnections.domain.model


data class GameProgress(
    val puzzleId: String,
    val solvedGroups: List<String>,
    val remainingAttempts: Int,
    val selectedWords: List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)