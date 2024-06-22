package com.pasha.ytodo

import android.app.Application
import com.pasha.ytodo.data.repositories.TodoItemsRepositoryTestImpl
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository

class TodoApplication : Application(), TodoItemRepositoryProvider {
    override val todoItemsRepository: TodoItemsRepository = TodoItemsRepositoryTestImpl()
}