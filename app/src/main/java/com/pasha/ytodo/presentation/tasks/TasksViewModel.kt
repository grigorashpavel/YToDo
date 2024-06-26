package com.pasha.ytodo.presentation.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pasha.ytodo.domain.models.TaskProgress
import com.pasha.ytodo.domain.models.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TasksViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {
    private val _tasksVisibility = MutableStateFlow(true)
    val tasksVisibility get() = _tasksVisibility.asStateFlow()

    private val _items: MutableStateFlow<List<TodoItem>> = MutableStateFlow(listOf())
    val items get() = _items.asStateFlow()

    private val _finishedTasksCounter: MutableStateFlow<Int> = MutableStateFlow(0)
    val finishedTasksCounter get() = _finishedTasksCounter.asStateFlow()

    init {
        fetchTasksData()
        calculateDoneTasks()
    }

    private fun fetchTasksData() {
        viewModelScope.launch {
            todoItemsRepository.getTodoItems().combine(_tasksVisibility) { items, showAllItems ->
                if (showAllItems) {
                    items
                } else {
                    items.filter { it.progress == TaskProgress.TODO }
                }
            }.collect { items ->
                _items.value = items.toList()
            }
        }
    }

    fun changeDoneTasksVisibility() {
        _tasksVisibility.value = !_tasksVisibility.value
    }

    private fun calculateDoneTasks() {
        viewModelScope.launch {
            todoItemsRepository.getTodoItems().collect { items ->
                _finishedTasksCounter.value = items.count { it.progress == TaskProgress.DONE }
            }
        }
    }

    fun changeTaskProgress(task: TodoItem, newProgress: TaskProgress) {
        val changedTask = task.copy(progress = newProgress)
        todoItemsRepository.changeItem(changedTask)
        fetchTasksData()
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>, extras: CreationExtras
                ): T {
                    val repositoryProvider =
                        checkNotNull(extras[APPLICATION_KEY]) as TodoItemRepositoryProvider
                    return TasksViewModel(repositoryProvider.todoItemsRepository) as T
                }
            }
        }
    }
}