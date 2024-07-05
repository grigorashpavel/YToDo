package com.pasha.ytodo.core

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
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
            repository.synchronizeItems()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val PERIOD_TAG = "PeriodicallySynchronizeWorker"
        const val ONE_TIME_TAG = "OneTimeSynchronizeWorker"
        const val MIN_BACK_OFF_MINUTES: Long = 5
        const val SYNCHRONIZE_PERIOD_HRS: Long = 8

        private fun createPeriodConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .setRequiresBatteryNotLow(true)
                .setRequiresDeviceIdle(true)
                .build()
        }

        private fun createOneTimeConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .build()
        }

        fun createPeriodRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<SynchronizeWorker>(
                SYNCHRONIZE_PERIOD_HRS, TimeUnit.HOURS
            )
                .setConstraints(createPeriodConstraints())
                .build()
        }



        fun createOneTimeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NetworkAvailableWorker>()
                .setConstraints(createOneTimeConstraints())
                .build()
        }
    }
}