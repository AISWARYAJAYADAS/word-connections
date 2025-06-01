package com.aiswarya.wordconnections.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzles")
data class PuzzleEntity(
    @PrimaryKey
    val puzzleId: String,
    val title: String,
    val difficulty: String,
    val remainingAttempts: Int = 4,  // Add this with default value
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val score: Int = 0
)

@Entity(tableName = "puzzle_groups")
data class PuzzleGroupEntity(
    @PrimaryKey
    val groupId: String,
    val puzzleId: String,
    val theme: String,
    val difficulty: Int,
    val color: String
)

@Entity(tableName = "puzzle_words")
data class PuzzleWordEntity(
    @PrimaryKey
    val wordId: String,
    val puzzleId: String,
    val groupId: String,
    val word: String,
    val position: Int,
    val isSolved: Boolean = false // Add this field
)