package com.pasha.ytodo.domain.repositories

import com.pasha.ytodo.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface TodoItemsRepository {
    fun getTodoItems(): Flow<List<TodoItem>>
    val items: Flow<List<TodoItem>>
    fun addTodoItem(item: TodoItem)
    fun deleteTodoItem(item: TodoItem)
    fun changeItem(item: TodoItem)
}