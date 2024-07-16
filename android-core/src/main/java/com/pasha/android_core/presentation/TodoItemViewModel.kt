package com.pasha.android_core.presentation

import androidx.lifecycle.ViewModel
import com.pasha.domain.entities.TodoItem


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