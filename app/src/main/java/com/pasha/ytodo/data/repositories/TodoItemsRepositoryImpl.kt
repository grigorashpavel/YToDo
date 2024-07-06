package com.pasha.ytodo.data.repositories


import android.util.Log
import com.pasha.ytodo.data.sources.local.LocalTodos
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class TodoItemsRepositoryImpl(
    private val localSource: DataSource,
    private val remoteSource: DataSource
) : TodoItemsRepository {
    private val _errors = MutableSharedFlow<Throwable>(replay = 2)
    override val errors: Flow<Throwable> get() = _errors.asSharedFlow()

    private val handlerException = CoroutineExceptionHandler { _, throwable ->
        _errors.tryEmit(throwable)
    }

    private val job = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + job + handlerException)

    override fun fetchTodoItems() {
        repositoryScope.launch {
            try {
                remoteSource.getTodoList()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun getTodoItemsFlow(): Flow<List<TodoItem>> {
        return localSource.getTodoListFlow()
    }

    override fun addTodoItem(item: TodoItem) {
        repositoryScope.launch {
            val addedItem = localSource.addTodoItem(item)
            remoteSource.addTodoItem(addedItem)

        }
    }

    override fun deleteTodoItem(item: TodoItem) {
        repositoryScope.launch {
            localSource.deleteTodoItemById(item.id)
            remoteSource.deleteTodoItemById(item.id)

        }
    }

    override fun changeItem(item: TodoItem) {
        repositoryScope.launch {
            localSource.updateTodoItemById(item.id, item)
            remoteSource.updateTodoItemById(item.id, item)

        }
    }
}