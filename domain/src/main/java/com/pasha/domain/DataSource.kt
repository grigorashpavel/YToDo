package com.pasha.domain


import com.pasha.domain.entities.TodoItem

interface DataSource {
    suspend fun getRevision(): Int?

    suspend fun getTodoList(): List<TodoItem>

    suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem>

    suspend fun getTodoItemById(todoId: String): TodoItem

    suspend fun addTodoItem(todoItem: TodoItem): TodoItem

    suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem

    suspend fun deleteTodoItemById(todoId: String): TodoItem
}