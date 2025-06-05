package com.aiswarya.wordconnections.data.repository

import android.util.Log
import com.aiswarya.wordconnections.data.local.dao.PuzzleDao
import com.aiswarya.wordconnections.data.local.entity.PuzzleEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleGroupEntity
import com.aiswarya.wordconnections.data.local.entity.PuzzleWordEntity
import com.aiswarya.wordconnections.data.mapper.PuzzleMapper
import com.aiswarya.wordconnections.data.remote.api.PuzzleApiService
import com.aiswarya.wordconnections.data.remote.dto.EnhancedPuzzleResponseDto
import com.aiswarya.wordconnections.domain.model.Difficulty
import com.aiswarya.wordconnections.domain.model.GameProgress
import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.model.PuzzleEntities
import com.aiswarya.wordconnections.domain.model.PuzzleGroup
import com.aiswarya.wordconnections.domain.model.ValidationResult
import com.aiswarya.wordconnections.domain.repository.PuzzleRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleRepositoryImpl @Inject constructor(
    private val apiService: PuzzleApiService,
    private val puzzleDao: PuzzleDao,
    private val puzzleMapper: PuzzleMapper
) : PuzzleRepository {

    companion object {
        private const val TAG = "PuzzleRepository"
        private const val MAX_CACHED_PUZZLES = 10

        // Hardcoded default puzzle for offline fallback
        private val DEFAULT_PUZZLE = Puzzle(
            puzzleId = "default_puzzle",
            title = "Default Puzzle",
            words = listOf(
                "DOG", "CAT", "BIRD", "FISH",
                "RED", "BLUE", "GREEN", "YELLOW",
                "CAR", "BUS", "TRAIN", "BIKE",
                "APPLE", "BANANA", "ORANGE", "GRAPE"
            ),
            groups = listOf(
                PuzzleGroup(
                    groupId = "default_group_1",
                    theme = "PETS",
                    words = listOf("DOG", "CAT", "BIRD", "FISH"),
                    difficulty = Difficulty.YELLOW,
                    color = "yellow"
                ),
                PuzzleGroup(
                    groupId = "default_group_2",
                    theme = "COLORS",
                    words = listOf("RED", "BLUE", "GREEN", "YELLOW"),
                    difficulty = Difficulty.GREEN,
                    color = "green"
                ),
                PuzzleGroup(
                    groupId = "default_group_3",
                    theme = "VEHICLES",
                    words = listOf("CAR", "BUS", "TRAIN", "BIKE"),
                    difficulty = Difficulty.BLUE,
                    color = "blue"
                ),
                PuzzleGroup(
                    groupId = "default_group_4",
                    theme = "FRUITS",
                    words = listOf("APPLE", "BANANA", "ORANGE", "GRAPE"),
                    difficulty = Difficulty.PURPLE,
                    color = "purple"
                )
            ),
            seed = 0,
            generatedAt = "2025-06-05",
            difficulty = "yellow, green, blue, purple"
        )
    }

    override suspend fun getPuzzle(seed: Int?): Result<Puzzle> {
        if (seed == null) { // Check cache for non-seeded requests
            getCachedPuzzle()?.let { return Result.success(it) }
        }
        return try {
            val response = apiService.getEnhancedPuzzle(seed)
            savePuzzle(response) // Use public method
            Result.success(puzzleMapper.toDomain(response))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch puzzle: ${e.message}")
            if (seed == null) { // Fall back to cache or default for non-seeded requests
                getCachedPuzzle()?.let { return Result.success(it) }
                saveDefaultPuzzle() // Save default puzzle to Room
                return Result.success(DEFAULT_PUZZLE)
            } else {
                saveDefaultPuzzle() // Save default puzzle to Room
                Result.success(DEFAULT_PUZZLE) // Use default for seeded requests too
            }
        }
    }

    override suspend fun getEnhancedPuzzle(seed: Int?): Result<Puzzle> {
        return try {
            val response = apiService.getEnhancedPuzzle(seed)
            savePuzzle(response) // Use public method
            Result.success(puzzleMapper.toDomain(response))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch enhanced puzzle: ${e.message}")
            // Fall back to cache or default for all requests
            getCachedPuzzle()?.let { return Result.success(it) }
            saveDefaultPuzzle() // Save default puzzle to Room
            Result.success(DEFAULT_PUZZLE)
        }
    }

    override suspend fun validateGuess(puzzleId: String, words: List<String>): Result<ValidationResult> {
        // For default puzzle, simulate validation locally
        if (puzzleId == DEFAULT_PUZZLE.puzzleId) {
            val group = DEFAULT_PUZZLE.groups.find { it.words.containsAll(words) }
            val progress = getPuzzleProgress(puzzleId) ?: GameProgress(puzzleId, emptyList(), 4)
            val newAttempts = if (group == null) progress.remainingAttempts - 1 else progress.remainingAttempts
            val newSolvedCategories = if (group != null) progress.solvedGroups + listOfNotNull(group.theme) else progress.solvedGroups
            savePuzzleProgress(puzzleId, progress.copy(remainingAttempts = newAttempts, solvedGroups = newSolvedCategories))
            return Result.success(
                ValidationResult(
                    isCorrect = group != null,
                    category = group?.theme,
                    remainingAttempts = newAttempts,
                    solvedCategories = newSolvedCategories,
                    isOneAway = false // Add logic to check if one word is incorrect
                )
            )
        }
        return try {
            val response = apiService.validateGuess(
                com.aiswarya.wordconnections.data.remote.dto.ValidateGuessRequest(puzzleId, words)
            )
            Result.success(
                ValidationResult(
                    isCorrect = response.isCorrect,
                    category = response.category,
                    remainingAttempts = response.remainingAttempts,
                    solvedCategories = response.solvedCategories,
                    isOneAway = response.isOneAway
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun savePuzzleProgress(puzzleId: String, progress: GameProgress) {
        // Save progress for all puzzles, including default puzzle
        puzzleDao.getPuzzleById(puzzleId)?.let { puzzle ->
            puzzleDao.updatePuzzle(puzzle.copy(
                isCompleted = progress.solvedGroups.size == 4,
                remainingAttempts = progress.remainingAttempts
            ))
            // Update word entities for solved groups
            progress.solvedGroups.forEach { theme ->
                val group = puzzleDao.getGroupsForPuzzle(puzzleId).find { it.theme == theme }
                group?.let {
                    puzzleDao.getWordsForGroup(it.groupId).forEach { word ->
                        puzzleDao.insertWords(listOf(word.copy(isSolved = true)))
                    }
                }
            }
        }
    }

    override suspend fun getPuzzleProgress(puzzleId: String): GameProgress? {
        return puzzleDao.getPuzzleById(puzzleId)?.let { puzzle ->
            val solvedGroups = puzzleDao.getGroupsForPuzzle(puzzleId)
                .filter { group ->
                    puzzleDao.getWordsForGroup(group.groupId).all { it.isSolved }
                }
                .map { it.theme }
            GameProgress(
                puzzleId = puzzleId,
                solvedGroups = solvedGroups,
                remainingAttempts = puzzle.remainingAttempts
            )
        } ?: if (puzzleId == DEFAULT_PUZZLE.puzzleId) {
            // Return default progress if puzzle not in database
            GameProgress(
                puzzleId = puzzleId,
                solvedGroups = emptyList(),
                remainingAttempts = 4
            )
        } else {
            null
        }
    }

    private suspend fun getCachedPuzzle(): Puzzle? {
        return puzzleDao.getAllPuzzles().firstOrNull()?.firstOrNull()?.let { cachedPuzzle ->
            val groups = puzzleDao.getGroupsForPuzzle(cachedPuzzle.puzzleId)
            val words = puzzleDao.getWordsForPuzzle(cachedPuzzle.puzzleId)
            puzzleMapper.toDomain(cachedPuzzle, groups, words)
        }
    }

    // Public method for saving puzzle
    suspend fun savePuzzle(response: EnhancedPuzzleResponseDto) {
        savePuzzleToLocal(response)
    }

    private suspend fun savePuzzleToLocal(response: EnhancedPuzzleResponseDto) {
        val puzzleEntities = puzzleMapper.toEntity(response)
        puzzleDao.insertCompletePuzzle(
            puzzleEntities.puzzle,
            puzzleEntities.groups,
            puzzleEntities.words
        )
        clearOldPuzzles()
    }

    private suspend fun saveDefaultPuzzle() {
        // Check if default puzzle already exists
        if (puzzleDao.getPuzzleById(DEFAULT_PUZZLE.puzzleId) == null) {
            val puzzleEntities = defaultPuzzleToEntities()
            puzzleDao.insertCompletePuzzle(
                puzzleEntities.puzzle,
                puzzleEntities.groups,
                puzzleEntities.words
            )
            Log.d(TAG, "Saved default puzzle to Room database")
        }
    }

    private fun defaultPuzzleToEntities(): PuzzleEntities {
        val puzzleEntity = PuzzleEntity(
            puzzleId = DEFAULT_PUZZLE.puzzleId,
            title = DEFAULT_PUZZLE.title,
            difficulty = DEFAULT_PUZZLE.difficulty,
            createdAt = System.currentTimeMillis(),
            isCompleted = false,
            score = 0,
            remainingAttempts = 4
        )

        val groupEntities = DEFAULT_PUZZLE.groups.map { group ->
            PuzzleGroupEntity(
                groupId = group.groupId,
                puzzleId = DEFAULT_PUZZLE.puzzleId,
                theme = group.theme,
                difficulty = group.difficulty.ordinal + 1,
                color = group.color
            )
        }

        val wordEntities = DEFAULT_PUZZLE.groups.flatMapIndexed { _, group ->
            group.words.mapIndexed { wordIndex, word ->
                PuzzleWordEntity(
                    wordId = "${DEFAULT_PUZZLE.puzzleId}_${group.theme}_$wordIndex",
                    puzzleId = DEFAULT_PUZZLE.puzzleId,
                    groupId = group.groupId,
                    word = word,
                    position = wordIndex,
                    isSolved = false
                )
            }
        }

        return PuzzleEntities(puzzleEntity, groupEntities, wordEntities)
    }

    private suspend fun clearOldPuzzles() {
        val puzzles = puzzleDao.getAllPuzzles().firstOrNull() ?: return
        // Exclude default puzzle from deletion
        val nonDefaultPuzzles = puzzles.filter { it.puzzleId != DEFAULT_PUZZLE.puzzleId }
        if (nonDefaultPuzzles.size > MAX_CACHED_PUZZLES) {
            nonDefaultPuzzles.sortedByDescending { it.createdAt }
                .drop(MAX_CACHED_PUZZLES)
                .forEach { puzzleDao.deletePuzzle(it) }
        }
    }

    suspend fun getCachedPuzzleCount(): Int {
        return puzzleDao.getAllPuzzles().firstOrNull()?.size ?: 0
    }
}