package com.pasha.ytodo.data.sources.local

import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.domain.entities.TaskPriority
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.random.Random


private const val ITEMS_COUNT = 20

class LocalTodos : DataSource {
    private val items = mutableListOf<TodoItem>()
    private val itemsFlow: MutableStateFlow<List<TodoItem>> = MutableStateFlow(emptyList())

    init {
        repeat(ITEMS_COUNT) {
            val newItem = generateItem()
            items.add(newItem)
        }

        itemsFlow.update { items.toList() }
    }

    fun getTodoListFlow(): StateFlow<List<TodoItem>> = itemsFlow.asStateFlow()

    override suspend fun getTodoList(): List<TodoItem> {
        return items.toList()
    }

    override suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem> {
        items.clear()
        items.addAll(newList)

        itemsFlow.update { items.toList() }

        return items
    }

    override suspend fun getTodoItemById(todoId: String): TodoItem {
        val item = items.find { UUID.fromString(it.id) == UUID.fromString(todoId) }

        if (item != null) {
            return item
        } else throw Exception("На устройстве указанное дело найдено не было.")
    }

    override suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem {
        for (i in items.indices) {
            val item = items[i]
            if (UUID.fromString(item.id) == UUID.fromString(todoId)) {
                items[i] = newItem
                itemsFlow.update { items.toList() }

                return newItem
            }
        }

        throw Exception("На устройстве указанное дело найдено не было.")
    }

    override suspend fun deleteTodoItemById(todoId: String): TodoItem {
        val isRemoved = items.removeIf { UUID.fromString(it.id) == UUID.fromString(todoId) }

        val todoItem = TodoItem(
            "",
            "",
            TaskPriority.NORMAL,
            null,
            TaskProgress.DONE,
            creationDate = LocalDateTime.now(),
            null
        )

        return if (isRemoved) {
            itemsFlow.update { items.toList() }
            todoItem
        } else throw Exception("При удалении возникла ошибка.")
    }

    override suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        items.add(todoItem)
        itemsFlow.update { items.toList() }

        return todoItem
    }

    fun throwException() {
        throw RuntimeException("Это замоканная ошибка для проверки.")
    }

    private fun generateItem(): TodoItem {
        return TodoItem(
            id = UUID.randomUUID().toString(),
            text = generateRandomText(),
            priority = generateRandomPriority(),
            deadline = generateRandomDate(2023, 2024, canBeNull = true),
            progress = generateRandomProgress(),
            creationDate = generateRandomDate(2017, 2020, canBeNull = false)!!,
            editDate = generateRandomDate(2020, 2024, canBeNull = true)
        )
    }

    private fun generateRandomText(): String {
        val charPool: List<Char> =
            ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf(' ', '.', ',', '!', '?', ';', ':')
        val randomLength = Random.nextInt(0, 201)
        return (1..randomLength)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun generateRandomPriority(): TaskPriority {
        return TaskPriority.entries.random()
    }

    private fun generateRandomDate(
        startYear: Int,
        endYear: Int,
        canBeNull: Boolean
    ): LocalDateTime? {
        val startDateTime = LocalDateTime.of(startYear, 1, 1, 0, 0)
        val endDateTime = LocalDateTime.of(endYear, 12, 31, 23, 59)

        val startEpochSecond = startDateTime.toEpochSecond(ZoneOffset.UTC)
        val endEpochSecond = endDateTime.toEpochSecond(ZoneOffset.UTC)

        val randomEpochSecond = Random.nextLong(startEpochSecond, endEpochSecond)
        val randomTime = LocalDateTime.ofEpochSecond(randomEpochSecond, 0, ZoneOffset.UTC)

        val mustReturnNull = listOf(0, 1, 2).random() == 0
        return if (mustReturnNull && canBeNull) null else randomTime
    }

    private fun generateRandomProgress(): TaskProgress {
        return TaskProgress.entries.random()
    }
}