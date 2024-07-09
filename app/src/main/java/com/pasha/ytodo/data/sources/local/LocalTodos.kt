package com.pasha.ytodo.data.sources.local

import com.pasha.ytodo.data.models.SynchronizeRoomEntity
import com.pasha.ytodo.data.sources.local.room.Converters
import com.pasha.ytodo.data.sources.local.room.TodoRoomDatabase
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.repositories.LocalDataSource
import kotlinx.coroutines.flow.Flow


class LocalTodos(
    private val database: TodoRoomDatabase
) : DataSource, LocalDataSource {
    override suspend fun getRevision(): Int? {
        return database.SynchronizeDao().getRevision()
    }

    override suspend fun setNewRevision(newRevision: Int) {
        database.SynchronizeDao()
            .updateSynchronizeInfo(
                SynchronizeRoomEntity(revision = newRevision, isModifiedWithoutConnection = false)
            )
    }

    override suspend fun setNoConnectionUpdate(isUpdated: Boolean) {
        val revision = database.SynchronizeDao().getRevision()
        database.SynchronizeDao().updateSynchronizeInfo(
            SynchronizeRoomEntity(revision = revision, isModifiedWithoutConnection = isUpdated)
        )
    }

    override suspend fun isModifiedWithoutConnection(): Boolean {
        return database.SynchronizeDao().isModifiedWithoutConnection()
    }

    override fun getTodoListFlow(): Flow<List<TodoItem>> =
        database.TodoDao().getTodoListFlow()

    override suspend fun getTodoList(): List<TodoItem> {
        return database.TodoDao().getTodoList()
    }

    override suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem> {
        if (newList.isNotEmpty()) {
            val oldItems = database.TodoDao().getTodoList()

            val deletedItems = oldItems.toMutableList().also { it.removeAll(newList) }
            val addedItems = newList.toMutableList().also { it.removeAll(oldItems) }

            deletedItems.forEach { item ->
                database.TodoDao().deleteItemById(item.id)
            }

            newList.forEach { item ->
                database.TodoDao().updateItem(Converters.fromTodoItem(item))
            }

            addedItems.forEach { item ->
                database.TodoDao().addItem(Converters.fromTodoItem(item))
            }
        } else database.TodoDao().deleteAllItems()

        return newList
    }

    override suspend fun getTodoItemById(todoId: String): TodoItem {
        return database.TodoDao().getItemById(todoId)
    }

    override suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem {
        database.TodoDao().updateItem(Converters.fromTodoItem(newItem))

        return newItem
    }

    override suspend fun deleteTodoItemById(todoId: String): TodoItem {
        val item = database.TodoDao().getItemById(todoId)
        database.TodoDao().deleteItemById(todoId)

        return item
    }

    override suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        database.TodoDao().addItem(Converters.fromTodoItem(todoItem))

        return todoItem
    }
}