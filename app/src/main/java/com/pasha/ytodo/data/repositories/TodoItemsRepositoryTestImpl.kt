package com.pasha.ytodo.data.repositories

import android.util.Log
import com.pasha.ytodo.domain.models.TaskPriority
import com.pasha.ytodo.domain.models.TaskProgress
import com.pasha.ytodo.domain.models.TodoItem
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.random.Random


private const val ITEMS_COUNT = 20

class TodoItemsRepositoryTestImpl : TodoItemsRepository {
    private val flow: MutableStateFlow<List<TodoItem>> = MutableStateFlow(listOf())
    private val repositoryScope = CoroutineScope(Dispatchers.IO + Job())

    init {
        setTestItems()
    }

    override fun getTodoItems(): Flow<List<TodoItem>> = flow.asStateFlow()

    override fun addTodoItem(item: TodoItem) {
        repositoryScope.launch {
            val oldList = flow.value
            val newList = oldList.toMutableList()

            newList.add(item)
            flow.value = newList
        }
    }

    override fun deleteTodoItem(item: TodoItem) {
        repositoryScope.launch {
            val list = flow.value.toMutableList()
            list.remove(item)
            flow.value = list
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

    private fun setTestItems() {
        repeat(ITEMS_COUNT) {
            val newItem = generateItem()
            addTodoItem(newItem)
        }
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