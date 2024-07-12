package com.pasha.edit.internal.compose.screen


import com.pasha.domain.entities.TaskPriority
import java.time.LocalDateTime


data class ChangeableTaskPart(
    val text: String = "",
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: LocalDateTime? = null
)
