package com.pasha.ytodo.core

import android.content.Context
import androidx.work.CoroutineWorker
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
    }
}