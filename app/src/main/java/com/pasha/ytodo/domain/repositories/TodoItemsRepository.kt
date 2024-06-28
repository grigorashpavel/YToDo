package com.pasha.ytodo.domain.repositories

import com.pasha.ytodo.domain.models.TodoItem
import com.pasha.ytodo.util.Error
import com.pasha.ytodo.util.Result
import kotlinx.coroutines.flow.Flow


interface TodoItemsRepository {
    fun getTodoItems(): Flow<List<TodoItem>>
    fun addTodoItem(item: TodoItem)
    fun deleteTodoItem(item: TodoItem)
    fun changeItem(item: TodoItem)

    val errors: Flow<Throwable>
}