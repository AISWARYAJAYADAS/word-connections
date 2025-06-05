package com.aiswarya.wordconnections

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import com.aiswarya.wordconnections.work.PuzzlePrefetchWorker
import dagger.hilt.android.HiltAndroidApp
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WordConnectionsApplication : Application() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        schedulePuzzlePrefetch()
    }

    private fun schedulePuzzlePrefetch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val prefetchRequest = PeriodicWorkRequestBuilder<PuzzlePrefetchWorker>(
            repeatInterval = 1, // Run daily
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PuzzlePrefetchWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            prefetchRequest
        )
    }
}