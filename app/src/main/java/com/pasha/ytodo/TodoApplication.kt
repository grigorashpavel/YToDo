package com.pasha.ytodo

import android.app.Application
import com.pasha.ytodo.data.repositories.TodoItemsRepositoryImpl
import com.pasha.ytodo.data.sources.local.LocalTodos
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository

class TodoApplication : Application(), TodoItemRepositoryProvider {
    private val local = LocalTodos()
    override val todoItemsRepository: TodoItemsRepository = TodoItemsRepositoryImpl(local)
}