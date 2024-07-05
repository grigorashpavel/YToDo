package com.pasha.ytodo.core

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class NetworkAvailableWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        (applicationContext as TodoItemRepositoryProvider).todoItemsRepository.fetchTodoItems()
        Result.success()
    }

    companion object {
        const val TAG = "NetworkAvailableWorker"

        fun startWorkerForOneRequest(context: Context) {
            Log.e("MainActivity", "startWorkerForOneRequest()")

            val workRequest = OneTimeWorkRequestBuilder<NetworkAvailableWorker>().build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                TAG,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}