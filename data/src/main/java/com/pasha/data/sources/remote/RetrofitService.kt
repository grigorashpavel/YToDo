package com.pasha.data.sources.remote

import android.content.Context
import com.pasha.data.R
import com.pasha.domain.DataSource
import com.pasha.domain.entities.TaskPriority
import com.pasha.domain.entities.TaskProgress
import com.pasha.domain.entities.TodoItem
import com.pasha.domain.repositories.IdentificationRepository
import com.pasha.models.TodoDto
import com.pasha.models.TodoListWrapper
import com.pasha.models.TodoWrapper
import com.pasha.network.TodoApi
import com.pasha.util.RequestException
import com.pasha.util.makeRequestForItem
import com.pasha.util.makeRequestForList
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class RetrofitService @Inject constructor(
    private val api: TodoApi,
    private val identificationRepository: IdentificationRepository,
    private val context: Context
) : DataSource {
    private val revision: AtomicInteger = AtomicInteger(NO_REVISION)

    private fun updateRevisionIfPossible(revision: Int?) {
        if (revision != null) this.revision.set(revision)
    }

    override suspend fun getRevision(): Int? {
        return if (revision.get() == NO_REVISION) null else revision.get()
    }

    override suspend fun getTodoList(): List<TodoItem> {
        val listWrapper = tryRequest {
            makeRequestForList(api::getTodoList)
        }

        updateRevisionIfPossible(listWrapper.revision)

        return listWrapper.todos.map { it.toTodoItem() }
    }

    override suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem> {
        val list = newList.map { it.toTodoDto() }

        val listWrapper = tryRequest {
            makeRequestForList {
                api.updateTodoList(
                    revision = revision.get(),
                    content = TodoListWrapper(status = "ok", todos = list)
                )
            }
        }

        updateRevisionIfPossible(listWrapper.revision)

        return listWrapper.todos.map { it.toTodoItem() }
    }

    override suspend fun getTodoItemById(todoId: String): TodoItem {
        val itemWrapper = tryRequest {
            makeRequestForItem { api.getTodoItemById(todoId) }
        }

        updateRevisionIfPossible(itemWrapper.revision)

        return itemWrapper.item.toTodoItem()
    }

    override suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        val itemWrapper = tryRequest {
            makeRequestForItem {
                api.addTodoItem(
                    revision = revision.get(),
                    content = TodoWrapper(status = "ok", item = todoItem.toTodoDto())
                )
            }
        }

        updateRevisionIfPossible(itemWrapper.revision)

        return itemWrapper.item.toTodoItem()
    }

    override suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem {
        val itemWrapper = tryRequest {
            makeRequestForItem {
                api.updateTodoItem(
                    revision = revision.get(),
                    todoId = todoId,
                    content = TodoWrapper(status = "ok", item = newItem.toTodoDto())
                )
            }
        }

        updateRevisionIfPossible(itemWrapper.revision)

        return itemWrapper.item.toTodoItem()
    }

    override suspend fun deleteTodoItemById(todoId: String): TodoItem {
        val itemWrapper = tryRequest {
            makeRequestForItem {
                api.deleteTodoItem(
                    revision = revision.get(),
                    todoId
                )
            }
        }

        updateRevisionIfPossible(itemWrapper.revision)

        return itemWrapper.item.toTodoItem()
    }

    private fun TodoItem.toTodoDto(): TodoDto {
        return TodoDto(
            id = id,
            text = text,
            importance = getImportanceFromPriority(priority),
            deadline = deadline?.toLong(),
            done = getDoneFromProgress(progress),
            createTime = creationDate.toLong(),
            changeTime = editDate?.toLong() ?: System.currentTimeMillis(),
            lastDeviceId = identificationRepository.getDeviceId(),
        )
    }

    private fun LocalDateTime.toLong(): Long {
        return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun getImportanceFromPriority(priority: TaskPriority): String {
        return when (priority) {
            TaskPriority.LOW -> "low"
            TaskPriority.NORMAL -> "basic"
            TaskPriority.HIGH -> "important"
        }
    }

    private fun getDoneFromProgress(progress: TaskProgress): Boolean {
        return when (progress) {
            TaskProgress.DONE -> true
            TaskProgress.TODO -> false
        }
    }

    companion object {
        private const val NO_REVISION = -1
    }

    private suspend fun <T> tryRequest(request: suspend () -> T): T {
        try {
            return request.invoke()
        } catch (e: RequestException.BadRequest) {
            throw Exception(context.getString(R.string.network_error_400))
        } catch (e: RequestException.Unauthorized) {
            throw Exception(context.getString(R.string.network_error_401))
        } catch (e: RequestException.NotFound) {
            throw Exception(context.getString(R.string.network_error_404))
        } catch (e: RequestException.InternalServer) {
            throw Exception(context.getString(R.string.network_error_400))
        } catch (e: RequestException.Timeout) {
            throw Exception(context.getString(R.string.network_error_timeout))
        } catch (e: RequestException.NoConnection) {
            throw Exception(context.getString(R.string.network_error_connection))
        } catch (e: RequestException.Timeout) {
            throw Exception(context.getString(R.string.network_error_timeout))
        } catch (e: RequestException.Unknown) {
            throw Exception(context.getString(R.string.unknown_error))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        throw Exception()
    }

    private fun TodoDto.toTodoItem(): TodoItem = TodoItem(
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