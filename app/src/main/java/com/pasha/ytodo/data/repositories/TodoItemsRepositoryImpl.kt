package com.pasha.ytodo.data.repositories


import android.util.Log
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.repositories.LocalDataSource
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


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

    override fun synchronizeLocalItems() {
        repositoryScope.launch {
            val remoteList = remoteSource.getTodoList()

            val remoteRevision = remoteSource.getRevision()!!
            val localRevision = localSource.getRevision()

            if (localRevision == null || remoteRevision > localRevision) {
                localSource.updateTodoList(newList = remoteList)
                (localSource as LocalDataSource).setNewRevision(remoteRevision)
            }
        }
    }

    override fun getTodoItemsFlow(): Flow<List<TodoItem>> {
        return (localSource as LocalDataSource).getTodoListFlow()
    }

    override fun addTodoItem(item: TodoItem) {
        repositoryScope.launch {
            remoteSource.getTodoList()

            tryToSendRequest {
                val addedItem = localSource.addTodoItem(item)
                remoteSource.addTodoItem(addedItem)
            }
        }
    }

    override fun deleteTodoItem(item: TodoItem) {
        repositoryScope.launch {
            remoteSource.getTodoList()

            tryToSendRequest {
                localSource.deleteTodoItemById(item.id)
                remoteSource.deleteTodoItemById(item.id)
            }
        }
    }

    override fun changeItem(item: TodoItem) {
        repositoryScope.launch {
            remoteSource.getTodoList()

            tryToSendRequest {
                localSource.updateTodoItemById(item.id, item)
                remoteSource.updateTodoItemById(item.id, item)
            }
        }
    }

    private suspend fun tryToSendRequest(call: suspend () -> Unit) {
        try {
            call.invoke()
        } catch (e: Exception) {
            (localSource as LocalDataSource).setNoConnectionUpdate(true)

            throw e
        }
    }
}