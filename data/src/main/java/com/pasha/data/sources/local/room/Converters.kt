package com.pasha.data.sources.local.room

import androidx.room.TypeConverter
import com.pasha.data.sources.local.room.models.TodoRoomEntity
import com.pasha.domain.entities.TaskPriority
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toUUID(uuidString: String): UUID {
        return UUID.fromString(uuidString)
    }

    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority): String {
        return when(priority) {
            TaskPriority.LOW -> "low"
            TaskPriority.NORMAL -> "basic"
            TaskPriority.HIGH -> "important"
        }
    }

    @TypeConverter
    @JvmStatic
    fun toTaskPriority(priorityString: String): TaskPriority {
        return when(priorityString) {
            "low" -> TaskPriority.LOW
            "basic" -> TaskPriority.NORMAL
            "important" -> TaskPriority.HIGH
            //never
            else -> TaskPriority.NORMAL
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromTaskProgress(progress: TaskProgress): Boolean {
        return when(progress) {
            TaskProgress.DONE -> true
            TaskProgress.TODO -> false
        }
    }

    @TypeConverter
    @JvmStatic
    fun toTaskProgress(progressBool: Boolean): TaskProgress {
        return when(progressBool) {
            true -> TaskProgress.DONE
            false -> TaskProgress.TODO
        }
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(date: LocalDateTime): Long {
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @TypeConverter
    @JvmStatic
    fun toTodoItem(entity: TodoRoomEntity): TodoItem {
        return TodoItem(
            id = entity.id.toString(),
            text = entity.text,
            priority = entity.priority,
            deadline = entity.deadline,
            progress = entity.progress,
            creationDate = entity.creationDate,
            editDate = entity.editDate
        )
    }

    @TypeConverter
    @JvmStatic
    fun fromTodoItem(item: TodoItem): TodoRoomEntity {
        return TodoRoomEntity(
            id = UUID.fromString(item.id),
            text = item.text,
            priority = item.priority,
            deadline = item.deadline,
            progress = item.progress,
            creationDate = item.creationDate,
            editDate = item.editDate
        )
    }
}