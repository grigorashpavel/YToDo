package com.pasha.ytodo.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TasksViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {
    private val _tasksVisibility = MutableStateFlow(true)
    val tasksVisibility get() = _tasksVisibility.asStateFlow()

    private val _items: MutableStateFlow<List<TodoItem>> = MutableStateFlow(listOf())
    val items get() = _items.asStateFlow()

    private val _finishedTasksCounter: MutableStateFlow<Int> = MutableStateFlow(0)
    val finishedTasksCounter get() = _finishedTasksCounter.asStateFlow()

    private val _isListRefreshing = MutableStateFlow(true)
    val isListRefreshing get() = _isListRefreshing.asStateFlow()

    private val _errors: MutableStateFlow<String?> = MutableStateFlow(null)
    val errors = _errors.asStateFlow()

    init {
        fetchAndCollectTasksData()
        calculateDoneTasks()
        initErrorsFlow()
    }

    private var loadListJob: Job? = null

    fun refreshTodoList() {
        loadListJob?.cancel()

        startRefresh()
        fetchAndCollectTasksData()
    }

    private fun startRefresh() {
        _isListRefreshing.update { true }
    }

    private fun endRefresh() {
        _isListRefreshing.update { false }
    }

    private fun fetchAndCollectTasksData() {
        todoItemsRepository.fetchTodoItems()

        loadListJob = viewModelScope.launch {
            if (isActive.not()) return@launch

            todoItemsRepository.getTodoItemsFlow().combine(_tasksVisibility) { items, showAllItems ->
                if (showAllItems) {
                    items
                } else {
                    items.filter { it.progress == TaskProgress.TODO }
                }
            }.collect { items ->
                endRefresh()
                _items.update { items.toList() }
            }
        }
    }

    private fun initErrorsFlow() {
        viewModelScope.launch {
            todoItemsRepository.errors.collect { throws ->
                _errors.update { throws.message }
            }
        }
    }

    fun errorMessageShown() {
        _errors.update { null }
    }

    fun changeDoneTasksVisibility() {
        _tasksVisibility.update { tasksVisibility.value.not() }
    }

    private fun calculateDoneTasks() {
        viewModelScope.launch {
            todoItemsRepository.getTodoItemsFlow().collect { items ->
                _finishedTasksCounter.update {
                    items.count { it.progress == TaskProgress.DONE }
                }
            }
        }
    }

    fun changeTaskProgress(task: TodoItem, newProgress: TaskProgress) {
        val changedTask = task.copy(progress = newProgress)
        viewModelScope.launch {
            todoItemsRepository.changeItem(changedTask)
        }
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