package com.pasha.ytodo.data.sources.local

import com.pasha.ytodo.data.models.RevisionRoomEntity
import com.pasha.ytodo.data.sources.local.room.Converters
import com.pasha.ytodo.data.sources.local.room.TodoRoomDatabase
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow


class LocalTodos(
    private val database: TodoRoomDatabase
) : DataSource {
    override fun getRevision(): Int? {
        val revision = database.RevisionDao().getRevision()
        if (revision == null) incrementRevision()

        return database.RevisionDao().getRevision()
    }

    private fun incrementRevision() {
        val oldRevision = database.RevisionDao().getRevision()

        if (oldRevision == null) {
            setNewRevision(1)
        } else {
            setNewRevision(oldRevision + 1)
        }
    }

    override fun setNewRevision(newRevision: Int) {
        database.RevisionDao().updateRevision(RevisionRoomEntity(revision = newRevision))
    }

    override fun getTodoListFlow(): Flow<List<TodoItem>> =
        database.TodoDao().getTodoListFlow()

    override suspend fun getTodoList(): List<TodoItem> {
        return database.TodoDao().getTodoList()
    }

    override suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem> {
        database.TodoDao().deleteAllItems()
        database.TodoDao().addAllItems(newList.map { Converters.fromTodoItem(it) })

        incrementRevision()
        return newList
    }

    override suspend fun getTodoItemById(todoId: String): TodoItem {
        return database.TodoDao().getItemById(todoId)
    }

    override suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem {
        database.TodoDao().updateItem(Converters.fromTodoItem(newItem))

        incrementRevision()
        return newItem
    }

    override suspend fun deleteTodoItemById(todoId: String): TodoItem {
        val item = database.TodoDao().getItemById(todoId)
        database.TodoDao().deleteItemById(todoId)

        incrementRevision()
        return item
    }

    override suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        database.TodoDao().addItem(Converters.fromTodoItem(todoItem))

        incrementRevision()
        return todoItem
    }
}