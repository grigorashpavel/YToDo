package com.pasha.ytodo.domain


import com.pasha.ytodo.domain.entities.TodoItem

interface DataSource {
    suspend fun getTodoList(): List<TodoItem>

    suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem>

    suspend fun getTodoItemById(todoId: String): TodoItem

    suspend fun addTodoItem(todoItem: TodoItem): TodoItem

    suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem

    suspend fun deleteTodoItemById(todoId: String): TodoItem
}