package com.aiswarya.wordconnections.data.repository

import android.util.Log
import com.aiswarya.wordconnections.data.local.dao.PuzzleDao
import com.aiswarya.wordconnections.data.mapper.PuzzleMapper
import com.aiswarya.wordconnections.data.remote.api.PuzzleApiService
import com.aiswarya.wordconnections.data.remote.dto.EnhancedPuzzleResponseDto
import com.aiswarya.wordconnections.domain.model.Difficulty
import com.aiswarya.wordconnections.domain.model.GameProgress
import com.aiswarya.wordconnections.domain.model.Puzzle
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
                getCachedPuzzle()?.let { Result.success(it) }
                    ?: Result.success(DEFAULT_PUZZLE)
            } else {
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
            getCachedPuzzle()?.let { Result.success(it) }
                ?: Result.success(DEFAULT_PUZZLE)
        }
    }

    override suspend fun validateGuess(puzzleId: String, words: List<String>): Result<ValidationResult> {
        // For default puzzle, simulate validation locally
        if (puzzleId == DEFAULT_PUZZLE.puzzleId) {
            val group = DEFAULT_PUZZLE.groups.find { group ->
                group.words.containsAll(words)
            }
            return Result.success(
                ValidationResult(
                    isCorrect = group != null,
                    category = group?.theme,
                    remainingAttempts = 4, // Default puzzle doesn't track attempts
                    solvedCategories = if (group != null) listOf(group.theme) else emptyList(),
                    isOneAway = false // Simplified for default puzzle
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
        if (puzzleId == DEFAULT_PUZZLE.puzzleId) return // Skip saving progress for default puzzle
        puzzleDao.getPuzzleById(puzzleId)?.let { puzzle ->
            puzzleDao.updatePuzzle(puzzle.copy(
                isCompleted = progress.solvedGroups.size == 4,
                remainingAttempts = progress.remainingAttempts
            ))
        }
    }

    override suspend fun getPuzzleProgress(puzzleId: String): GameProgress? {
        if (puzzleId == DEFAULT_PUZZLE.puzzleId) {
            return GameProgress(
                puzzleId = puzzleId,
                solvedGroups = emptyList(),
                remainingAttempts = 4
            )
        }
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

    private suspend fun clearOldPuzzles() {
        val puzzles = puzzleDao.getAllPuzzles().firstOrNull() ?: return
        if (puzzles.size > MAX_CACHED_PUZZLES) {
            puzzles.drop(MAX_CACHED_PUZZLES).forEach { puzzleDao.deletePuzzle(it) }
        }
    }

    suspend fun getCachedPuzzleCount(): Int {
        return puzzleDao.getAllPuzzles().firstOrNull()?.size ?: 0
    }
}