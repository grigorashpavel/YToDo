package com.pasha.ytodo.presentation.edit.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.domain.models.TaskProgress
import com.pasha.ytodo.domain.models.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class EditTaskViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    var editState = EditTaskState()
        private set

    fun changePriority(newPriority: TaskPriority) {
        editState = editState.copy(priority = newPriority)
    }

    fun changeDeadline(newDeadline: LocalDateTime?) {
        editState = editState.copy(deadline = newDeadline)
    }

    fun changeTask(oldTask: TodoItem, newText: String) {
        viewModelScope.launch {
            val changedItem = oldTask.copy(
                text = newText.trim(),
                priority = editState.priority,
                deadline = editState.deadline
            )

            todoItemsRepository.changeItem(changedItem)
        }
    }

    fun createTask(text: String, currentTime: LocalDateTime) {
        val item = TodoItem(
            id = UUID.randomUUID().toString(),
            text,
            priority = editState.priority,
            deadline = editState.deadline,
            progress = TaskProgress.TODO,
            creationDate = currentTime,
            editDate = null
        )
        todoItemsRepository.addTodoItem(item)
    }

    fun removeTask(task: TodoItem) {
        todoItemsRepository.deleteTodoItem(item = task)
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
                    return EditTaskViewModel(repositoryProvider.todoItemsRepository) as T
                }
            }
        }
    }
}