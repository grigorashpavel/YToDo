package com.pasha.ytodo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.pasha.ytodo.domain.models.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository


class TodoItemViewModel : ViewModel() {
    var sharedTodoItem: TodoItem? = null
        private set

    fun passTodoItem(item: TodoItem) {
        sharedTodoItem = item
    }

    fun clearSharedItem() {
        sharedTodoItem = null
    }
}