package com.pasha.edit.internal.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pasha.domain.entities.TaskPriority
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import com.pasha.domain.repositories.TodoItemRepositoryProvider
import com.pasha.domain.repositories.TodoItemsRepository
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class EditTaskViewModel @AssistedInject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private val _editState = MutableStateFlow(EditTaskState())
    val editState get() = _editState.asStateFlow()

    fun changePriority(newPriority: TaskPriority) {
        _editState.update {
            _editState.value.copy(priority = newPriority)
        }
    }

    fun changeDeadline(newDeadline: LocalDateTime?) {
        _editState.update {
            _editState.value.copy(deadline = newDeadline)
        }
    }

    fun changeTask(oldTask: TodoItem, newText: String) {
        viewModelScope.launch {
            val changedItem = oldTask.copy(
                text = newText.trim(),
                priority = _editState.value.priority,
                deadline = _editState.value.deadline
            )

            todoItemsRepository.changeItem(changedItem)
        }
    }

    fun createTask(text: String, currentTime: LocalDateTime) {
        val item = TodoItem(
            id = UUID.randomUUID().toString(),
            text,
            priority = _editState.value.priority,
            deadline = _editState.value.deadline,
            progress = TaskProgress.TODO,
            creationDate = currentTime,
            editDate = null
        )
        todoItemsRepository.addTodoItem(item)
    }

    fun removeTask(task: TodoItem) {
        viewModelScope.launch {
            todoItemsRepository.deleteTodoItem(item = task)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): EditTaskViewModel
    }
}