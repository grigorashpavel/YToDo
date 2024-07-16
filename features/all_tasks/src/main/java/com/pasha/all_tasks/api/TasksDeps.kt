package com.pasha.all_tasks.api

import com.pasha.android_core.di.Dependencies
import com.pasha.android_core.navigation.NavCommand
import com.pasha.domain.repositories.TodoItemsRepository


interface TasksDeps: Dependencies {
    val todoRepository: TodoItemsRepository
}

interface TasksNavDeps: Dependencies {
    val navCommandsProvider: TasksNavCommandsProvider
}

interface TasksNavCommandsProvider {
    val toTaskEdit: NavCommand
}