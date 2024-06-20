package com.pasha.ytodo.domain.repositories

import com.pasha.ytodo.domain.models.TodoItem

interface TodoItemRepositoryProvider {
    val todoItemsRepository: TodoItemsRepository
}