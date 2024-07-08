package com.pasha.ytodo.domain.repositories

interface TodoItemRepositoryProvider {
    val todoItemsRepository: TodoItemsRepository
}