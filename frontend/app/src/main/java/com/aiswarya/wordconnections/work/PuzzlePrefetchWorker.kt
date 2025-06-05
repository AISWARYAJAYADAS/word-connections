package com.aiswarya.wordconnections.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aiswarya.wordconnections.data.remote.api.PuzzleApiService
import com.aiswarya.wordconnections.data.repository.PuzzleRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PuzzlePrefetchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val puzzleRepository: PuzzleRepositoryImpl,
    private val apiService: PuzzleApiService
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "PuzzlePrefetchWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Check cache size
            val cacheCount = puzzleRepository.getCachedPuzzleCount()
            if (cacheCount < 2) {
                // Fetch a new puzzle with a random seed
                val randomSeed = (System.currentTimeMillis() % 10000).toInt()
                val response = apiService.getEnhancedPuzzle(randomSeed)
                puzzleRepository.savePuzzle(response) // Use public method
                Result.success()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}