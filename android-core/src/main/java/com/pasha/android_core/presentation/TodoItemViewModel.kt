package com.pasha.android_core.presentation

import android.icu.util.TimeUnit
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasha.domain.entities.TodoItem
import com.pasha.domain.repositories.TodoItemsRepository
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TodoItemViewModel @AssistedInject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    var sharedTodoItem: TodoItem? = null
        private set

    fun passTodoItem(item: TodoItem) {
        sharedTodoItem = item
    }

    fun clearSharedItem() {
        sharedTodoItem = null
    }

    private var tasksToDeletion: MutableList<TodoItem> = mutableListOf()

    private val _timerFlow = MutableStateFlow(0)
    val timerFlow get() = _timerFlow.asStateFlow()

    private val _isDeletionStart = MutableStateFlow(false)
    val isDeletionStart get() = _isDeletionStart.asStateFlow()

    private val _taskToDeletionName: MutableStateFlow<String> = MutableStateFlow(updateTasksNames())
    val taskToDeletionName = _taskToDeletionName.asStateFlow()

    private fun updateTasksNames(): String {
        return if (tasksToDeletion.isEmpty()) ""
        else if (tasksToDeletion.size >= 3) {
            "Вы удаляете максимальное количество дел: 3"
        } else if (tasksToDeletion.size > 1) {
            "Вы удаляете несколько дел: ${tasksToDeletion.size}"
        }
        else if (tasksToDeletion.first().text.isEmpty()) {
            "Пустое дело"
        } else tasksToDeletion.first().text
    }


    private val _totalCancellationTime: Long = 5_000
    val totalCancellationTimeSec get() = _totalCancellationTime / 1000
    private val deleteTimer = object : CountDownTimer(_totalCancellationTime, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            viewModelScope.launch(Dispatchers.IO) {
                val secondsToRemain = (millisUntilFinished / 1000).toInt()

                _timerFlow.update { secondsToRemain }
            }
        }

        override fun onFinish() {
            _isDeletionStart.update { false }

            viewModelScope.launch {
                val tasks = tasksToDeletion.toList()
                tasksToDeletion.clear()

                tasks.forEach { task ->
                    todoItemsRepository.deleteTodoItem(task)
                }
            }
        }
    }

    fun removeTask(task: TodoItem) {
        if (tasksToDeletion.size >= 3) return

        if (!tasksToDeletion.contains(task)) {
            tasksToDeletion.add(task)
        }

        _isDeletionStart.update { true }
        _taskToDeletionName.update { updateTasksNames() }

        deleteTimer.start()
    }

    fun cancelDeleteTaskOperation() {
        deleteTimer.cancel()

        _isDeletionStart.update { false }

        tasksToDeletion.clear()
        _taskToDeletionName.update { updateTasksNames() }
    }

    @AssistedFactory
    interface Factory {
        fun create(): TodoItemViewModel
    }
}