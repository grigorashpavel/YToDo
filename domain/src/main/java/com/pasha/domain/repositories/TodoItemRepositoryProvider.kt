package com.pasha.domain.repositories

interface TodoItemRepositoryProvider {
    val todoItemsRepository: TodoItemsRepository
}