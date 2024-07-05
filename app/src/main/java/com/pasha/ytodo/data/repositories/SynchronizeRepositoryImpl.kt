package com.pasha.ytodo.data.repositories

import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.repositories.SynchronizeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SynchronizeRepositoryImpl(
    private val localSource: DataSource,
    private val remoteSource: DataSource
) : SynchronizeRepository {
    override suspend fun synchronizeItems() {
        coroutineScope {
            val newList = mutableListOf<TodoItem>()
            val itemsLists = listOf(
                async { remoteSource.getTodoList() },
                async { localSource.getTodoList() }
            ).awaitAll()

            for (i in itemsLists.indices) {
                newList.addAll(itemsLists[i])
            }

            launch { remoteSource.updateTodoList(newList) }
            launch { localSource.updateTodoList(newList) }
        }
    }
}