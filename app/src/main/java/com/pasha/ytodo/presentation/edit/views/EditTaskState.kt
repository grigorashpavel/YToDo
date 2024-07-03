package com.pasha.ytodo.presentation.edit.views

import com.pasha.ytodo.domain.entities.TaskPriority
import java.time.LocalDateTime

data class EditTaskState(
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: LocalDateTime? = null
)
