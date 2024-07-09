package com.pasha.ytodo.domain.repositories

import com.pasha.ytodo.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow


interface TodoItemsRepository {
    fun synchronizeLocalItems()
    fun getTodoItemsFlow(): Flow<List<TodoItem>>
    fun addTodoItem(item: TodoItem)
    fun deleteTodoItem(item: TodoItem)
    fun changeItem(item: TodoItem)

    val errors: Flow<Throwable>
}