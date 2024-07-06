package com.pasha.ytodo.core

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pasha.ytodo.domain.repositories.SynchronizeRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SynchronizeWorker(
    context: Context,
    workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repository = (applicationContext as SynchronizeRepositoryProvider)
            .synchronizeRepository

        try {
            val isSynchronizeNeed = repository.isSynchronizeNeed()
            Log.e(ONE_TIME_TAG, "isSynchronizeNeed = $isSynchronizeNeed")
            if (isSynchronizeNeed.not()) Result.success()
            repository.synchronizeItems()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val PERIOD_TAG = "PeriodicallySynchronizeWorker"
        const val ONE_TIME_TAG = "OneTimeSynchronizeWorker"
        const val SYNCHRONIZE_PERIOD_HRS: Long = 8

        private fun createPeriodConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .setRequiresBatteryNotLow(true)
                .setRequiresDeviceIdle(true)
                .build()
        }

        private fun createPeriodRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<SynchronizeWorker>(
                SYNCHRONIZE_PERIOD_HRS, TimeUnit.HOURS
            )
                .setConstraints(createPeriodConstraints())
                .build()
        }

        fun registerPeriodSynchronizeWork(context: Context) {
            val request = createPeriodRequest()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIOD_TAG,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )
        }

        private fun createOneTimeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<SynchronizeWorker>()
                .build()
        }

        fun startSynchronizeIfNeed(context: Context) {
            val request = createOneTimeRequest()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_TAG,
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}