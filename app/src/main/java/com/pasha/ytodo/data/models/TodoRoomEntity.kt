package com.pasha.ytodo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pasha.ytodo.domain.entities.TaskPriority
import com.pasha.ytodo.domain.entities.TaskProgress
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
