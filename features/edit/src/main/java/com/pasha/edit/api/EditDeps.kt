package com.pasha.edit.api

import com.pasha.android_core.di.Dependencies
import com.pasha.domain.repositories.TodoItemsRepository


interface EditDeps: Dependencies {
    val todoRepository: TodoItemsRepository
}