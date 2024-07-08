package com.pasha.ytodo.domain.entities

import androidx.annotation.StringRes
import com.pasha.ytodo.R

enum class TaskPriority(@StringRes val stringId: Int) {
    LOW(R.string.task_priority_low_text),
    NORMAL(R.string.task_priority_normal_text),
    HIGH(R.string.task_priority_high_text)
}