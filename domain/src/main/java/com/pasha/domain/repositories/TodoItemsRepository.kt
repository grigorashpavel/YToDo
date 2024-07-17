package com.pasha.domain.repositories

import com.pasha.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow


interface TodoItemsRepository {
    fun synchronizeLocalItems()
    fun getTodoItemsFlow(): Flow<List<TodoItem>>
    fun addTodoItem(item: TodoItem)
    suspend fun deleteTodoItem(item: TodoItem)
    fun changeItem(item: TodoItem)

    val errors: Flow<Throwable>
}