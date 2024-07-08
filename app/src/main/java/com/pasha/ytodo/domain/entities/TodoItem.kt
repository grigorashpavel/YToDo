package com.pasha.ytodo.domain.entities

import java.time.LocalDateTime

data class TodoItem(
    val id: String,
    val text: String,
    val priority: TaskPriority,
    val deadline: LocalDateTime?,
    val progress: TaskProgress,
    val creationDate: LocalDateTime,
    val editDate: LocalDateTime?
)
