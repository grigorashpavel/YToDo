package com.pasha.ytodo.data.repositories

import com.pasha.ytodo.data.sources.local.LocalTodos
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
    private val localSource: LocalTodos
) : TodoItemsRepository {
    private val _errors = MutableSharedFlow<Throwable>(replay = 2)
    override val errors: Flow<Throwable> get() = _errors.asSharedFlow()

    private val handlerException = CoroutineExceptionHandler { _, throwable ->
        _errors.tryEmit(throwable)
    }

    private val job = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + job + handlerException)

    private val flow: MutableStateFlow<List<TodoItem>> = MutableStateFlow(listOf())

    init {
        flow.update { localSource.getTestItems() }
    }

    override fun getTodoItems(): Flow<List<TodoItem>> = flow.asStateFlow()

    override fun addTodoItem(item: TodoItem) {
        repositoryScope.launch {
            val oldList = flow.value
            val newList = oldList.toMutableList()

            newList.add(item)
            flow.update { newList }
        }
    }

    override fun deleteTodoItem(item: TodoItem) {
        repositoryScope.launch {
            val list = flow.value.toMutableList()
            list.remove(item)
            flow.update { list }
        }
    }

    override fun changeItem(item: TodoItem) {
        repositoryScope.launch {
            val tempList = flow.value.toMutableList()
            for (i in tempList.indices) {
                if (UUID.fromString(tempList[i].id) == UUID.fromString(item.id)) {
                    tempList[i] = item
                    break
                }
            }
            flow.emit(tempList)
        }
    }
}