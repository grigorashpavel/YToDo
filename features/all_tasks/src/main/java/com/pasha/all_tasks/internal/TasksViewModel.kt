package com.pasha.all_tasks.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import com.pasha.domain.repositories.TodoItemsRepository
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TasksViewModel @AssistedInject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
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
        collectTasksData()
        calculateDoneTasks()
        initErrorsFlow()
    }

    private var loadListJob: Job? = null

    fun refreshTodoList() {
        loadListJob?.cancel()

        startRefresh()
        todoItemsRepository.synchronizeLocalItems()

        collectTasksData()
    }

    private fun startRefresh() {
        _isListRefreshing.update { true }
    }

    private fun endRefresh() {
        _isListRefreshing.update { false }
    }

    private fun collectTasksData() {
        loadListJob = viewModelScope.launch {
            if (isActive.not()) return@launch

            todoItemsRepository.getTodoItemsFlow()
                .combine(_tasksVisibility) { items, showAllItems ->
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

    fun removeItem(item: TodoItem) {
        todoItemsRepository.deleteTodoItem(item)
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

    @AssistedFactory
    interface Factory {
        fun create(): TasksViewModel
    }
}