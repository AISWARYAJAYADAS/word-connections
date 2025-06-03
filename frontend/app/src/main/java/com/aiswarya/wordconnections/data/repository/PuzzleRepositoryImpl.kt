package com.aiswarya.wordconnections.data.repository

import android.util.Log
import com.aiswarya.wordconnections.data.local.dao.PuzzleDao
import com.aiswarya.wordconnections.data.mapper.PuzzleMapper
import com.aiswarya.wordconnections.data.remote.api.PuzzleApiService
import com.aiswarya.wordconnections.data.remote.dto.EnhancedPuzzleResponseDto
import com.aiswarya.wordconnections.domain.model.GameProgress
import com.aiswarya.wordconnections.domain.model.Puzzle
import com.aiswarya.wordconnections.domain.model.ValidationResult
import com.aiswarya.wordconnections.domain.repository.PuzzleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeout
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
        private const val NETWORK_TIMEOUT = 30_000L
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY = 1000L
    }

    override suspend fun getPuzzle(seed: Int?): Result<Puzzle> {
        getCachedPuzzle()?.let { return Result.success(it) }
        var retryCount = 0
        var lastException: Exception? = null
        while (retryCount < MAX_RETRIES) {
            try {
                val response = withTimeout(NETWORK_TIMEOUT) {
                    apiService.getEnhancedPuzzle(seed)
                }
                savePuzzleToLocal(response)
                return Result.success(puzzleMapper.toDomain(response))
            } catch (e: Exception) {
                lastException = e
                retryCount++
                Log.w(TAG, "Attempt $retryCount failed: ${e.message}")
                if (retryCount < MAX_RETRIES) {
                    delay(RETRY_DELAY * retryCount)
                }
            }
        }
        return getCachedPuzzle()?.let { Result.success(it) }
            ?: Result.failure(lastException ?: Exception("Network error after $MAX_RETRIES attempts"))
    }

    override suspend fun getEnhancedPuzzle(seed: Int?): Result<Puzzle> {
        return try {
            val response = apiService.getEnhancedPuzzle(seed)
            savePuzzleToLocal(response)
            Result.success(puzzleMapper.toDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateGuess(puzzleId: String, words: List<String>): Result<ValidationResult> {
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
        puzzleDao.getPuzzleById(puzzleId)?.let { puzzle ->
            puzzleDao.updatePuzzle(puzzle.copy(
                isCompleted = progress.solvedGroups.size == 4,
                remainingAttempts = progress.remainingAttempts
            ))
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
        }
    }

    private suspend fun getCachedPuzzle(): Puzzle? {
        return puzzleDao.getAllPuzzles().firstOrNull()?.firstOrNull()?.let { cachedPuzzle ->
            val groups = puzzleDao.getGroupsForPuzzle(cachedPuzzle.puzzleId)
            val words = puzzleDao.getWordsForPuzzle(cachedPuzzle.puzzleId)
            puzzleMapper.toDomain(cachedPuzzle, groups, words)
        }
    }

    private suspend fun savePuzzleToLocal(response: EnhancedPuzzleResponseDto) {
        val puzzleEntities = puzzleMapper.toEntity(response)
        puzzleDao.insertCompletePuzzle(
            puzzleEntities.puzzle,
            puzzleEntities.groups,
            puzzleEntities.words
        )
    }
}