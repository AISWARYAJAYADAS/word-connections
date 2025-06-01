package com.aiswarya.wordconnections.domain.model


data class ValidationResult(
    val isCorrect: Boolean,
    val category: String?,
    val remainingAttempts: Int,
    val solvedCategories: List<String>,
    val isOneAway: Boolean
)