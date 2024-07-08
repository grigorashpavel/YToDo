package com.pasha.ytodo.data.models

import com.pasha.ytodo.domain.entities.TaskPriority
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@Serializable
data class TodoDto(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("importance")
    val importance: String,
    @SerialName("deadline")
    val deadline: Long? = null,
    @SerialName("done")
    val done: Boolean,
    @SerialName("color")
    val color: String? = null,
    @SerialName("created_at")
    val createTime: Long,
    @SerialName("changed_at")
    val changeTime: Long,
    @SerialName("last_updated_by")
    val lastDeviceId: String
) {
    fun toTodoItem(): TodoItem = TodoItem(
        id,
        text,
        importance.toPriority(),
        deadline?.toLocalDateTime(),
        done.toProgress(),
        createTime.toLocalDateTime(),
        changeTime.toLocalDateTime()
    )

    private fun String.toPriority(): TaskPriority = when (this) {
        "low" -> TaskPriority.LOW
        "important" -> TaskPriority.HIGH
        else -> TaskPriority.NORMAL
    }

    private fun Boolean.toProgress(): TaskProgress = when (this) {
        true -> TaskProgress.DONE
        false -> TaskProgress.TODO
    }

    private fun Long.toLocalDateTime(): LocalDateTime {
        val instant = Instant.ofEpochMilli(this)
        val formatted = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        return formatted
    }
}
