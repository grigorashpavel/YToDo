package com.pasha.ytodo.presentation.edit.compose

import com.pasha.ytodo.domain.models.TaskPriority
import java.time.LocalDateTime

data class ChangeableTaskPart(
    val text: String = "",
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: LocalDateTime? = null
)
