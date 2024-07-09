package com.pasha.ytodo.domain.repositories

import com.pasha.ytodo.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface LocalDataSource {
    suspend fun setNewRevision(newRevision: Int)

    suspend fun setNoConnectionUpdate(isUpdated: Boolean)

    suspend fun isModifiedWithoutConnection(): Boolean

    fun getTodoListFlow(): Flow<List<TodoItem>>
}