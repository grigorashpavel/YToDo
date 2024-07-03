package com.pasha.ytodo.data.sources.local

import com.pasha.ytodo.domain.entities.TaskPriority
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.random.Random


private const val ITEMS_COUNT = 20

class LocalTodos {
    fun getTestItems(): List<TodoItem> {
        val items = mutableListOf<TodoItem>()
        repeat(ITEMS_COUNT) {
            val newItem = generateItem()
            items.add(newItem)
        }

        return items
    }

    fun throwException() {
        throw RuntimeException("Это замоканная ошибка. Для проверки.")
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