package com.pasha.data.repositories


import com.pasha.android_core.di.RemoteDataSource
import com.pasha.domain.DataSource
import com.pasha.domain.LocalDataSource
import com.pasha.domain.entities.TodoItem
import com.pasha.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class TodoItemsRepositoryImpl @Inject constructor(
    @com.pasha.android_core.di.LocalDataSource private val localSource: DataSource,
    @RemoteDataSource private val remoteSource: DataSource
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
            val addedItem = localSource.addTodoItem(item)

            tryToSendRequest {
                remoteSource.addTodoItem(addedItem)
            }
        }
    }

    override suspend fun deleteTodoItem(item: TodoItem) {
        withContext(repositoryScope.coroutineContext) {
            localSource.deleteTodoItemById(item.id)

            try {
                tryToSendRequest {
                    remoteSource.deleteTodoItemById(item.id)
                }
            } catch (e: Exception) {
                _errors.tryEmit(e)
            }
        }
    }

    override fun changeItem(item: TodoItem) {
        repositoryScope.launch {
            localSource.updateTodoItemById(item.id, item)

            tryToSendRequest {
                remoteSource.updateTodoItemById(item.id, item)
            }
        }
    }

    private suspend fun tryToSendRequest(call: suspend () -> Unit) {
        try {
            remoteSource.getTodoList()
            call.invoke()
        } catch (e: Exception) {
            (localSource as LocalDataSource).setNoConnectionUpdate(true)

            throw e
        }
    }
}