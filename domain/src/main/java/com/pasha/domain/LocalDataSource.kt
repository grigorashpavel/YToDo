package com.pasha.domain

import com.pasha.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun setNewRevision(newRevision: Int)

    suspend fun setNoConnectionUpdate(isUpdated: Boolean)

    suspend fun isModifiedWithoutConnection(): Boolean

    fun getTodoListFlow(): Flow<List<TodoItem>>
}