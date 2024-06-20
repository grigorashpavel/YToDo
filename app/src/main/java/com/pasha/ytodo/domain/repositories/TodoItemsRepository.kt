package com.pasha.ytodo.domain.repositories

import com.pasha.ytodo.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemsRepository {
    fun getTodoItems(): Flow<List<TodoItem>>
    suspend fun addTodoItem(item: TodoItem)
}