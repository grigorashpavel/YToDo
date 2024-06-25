package com.pasha.ytodo.presentation.edit

import com.pasha.ytodo.domain.models.TaskPriority
import java.time.LocalDateTime

data class EditTaskState(
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: LocalDateTime? = null
)
