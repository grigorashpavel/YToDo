package com.pasha.data.sources.local.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pasha.domain.entities.TaskPriority
import com.pasha.domain.entities.TaskProgress
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "todos")
data class TodoRoomEntity(
    @PrimaryKey val id: UUID,
    val text: String,
    val priority: TaskPriority,
    val deadline: LocalDateTime?,
    val progress: TaskProgress,
    val creationDate: LocalDateTime,
    val editDate: LocalDateTime?
)
