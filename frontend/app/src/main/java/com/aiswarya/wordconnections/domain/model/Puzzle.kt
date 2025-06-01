package com.aiswarya.wordconnections.domain.model

data class Puzzle(
    val puzzleId: String,
    val title: String,
    val words: List<String>,
    val groups: List<PuzzleGroup>,
    val seed: Int,
    val generatedAt: String,
    val difficulty: String
)

data class PuzzleGroup(
    val groupId: String,
    val theme: String,
    val words: List<String>,
    val difficulty: Difficulty,
    val color: String
)

enum class Difficulty(val color: String) {
    YELLOW("yellow"),
    GREEN("green"),
    BLUE("blue"),
    PURPLE("purple");

    companion object {
        fun fromString(value: String): Difficulty {
            return when (value.lowercase()) {
                "yellow" -> YELLOW
                "green" -> GREEN
                "blue" -> BLUE
                "purple" -> PURPLE
                else -> YELLOW
            }
        }
    }
}