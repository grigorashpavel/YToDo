package com.pasha.ytodo.presentation.edit.compose.screen

import com.pasha.ytodo.domain.entities.TaskPriority
import java.time.LocalDateTime

data class ChangeableTaskPart(
    val text: String = "",
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: LocalDateTime? = null
)
