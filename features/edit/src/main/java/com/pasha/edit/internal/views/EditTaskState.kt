package com.pasha.edit.internal.views

import com.pasha.domain.entities.TaskPriority
import java.time.LocalDateTime


data class EditTaskState(
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: LocalDateTime? = null
)
