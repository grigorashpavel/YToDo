package com.pasha.ytodo.data.sources.remote

import com.pasha.ytodo.core.DeviceIdentificationManager
import com.pasha.ytodo.data.models.TodoDto
import com.pasha.ytodo.data.models.TodoListWrapper
import com.pasha.ytodo.data.models.TodoWrapper
import com.pasha.ytodo.domain.entities.TaskPriority
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.DataSource
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger

class RetrofitService(
    private val api: TodoApi,
    private val identificationManager: DeviceIdentificationManager
) : DataSource {
    private val revision: AtomicInteger = AtomicInteger(0)

    private fun updateRevisionIfPossible(revision: Int?) {
        if (revision != null) this.revision.set(revision)
    }

    override suspend fun getTodoList(): List<TodoItem> {
        val listWrapper = makeRequestForList { api.getTodoList() }

        return listWrapper.todos.map { it.toTodoItem() }
    }

    override suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem> {
        val list = newList.map { it.toTodoDto() }
        val listWrapper = makeRequestForList {
            api.updateTodoList(
                revision = revision.get(),
                content = TodoListWrapper(status = "ok", todos = list)
            )
        }

        return listWrapper.todos.map { it.toTodoItem() }
    }

    override suspend fun getTodoItemById(todoId: String): TodoItem {
        val itemWrapper = makeRequestForItem { api.getTodoItemById(todoId) }

        return itemWrapper.item.toTodoItem()
    }

    override suspend fun addTodoItem(todoItem: TodoItem): TodoItem {
        val itemWrapper = makeRequestForItem {
            api.addTodoItem(
                revision = revision.get(),
                content = TodoWrapper(status = "ok", item = todoItem.toTodoDto())
            )
        }

        return itemWrapper.item.toTodoItem()
    }

    override suspend fun updateTodoItemById(todoId: String, newItem: TodoItem): TodoItem {
        val itemWrapper = makeRequestForItem {
            api.updateTodoItem(
                revision = revision.get(),
                todoId = todoId,
                content = TodoWrapper(status = "ok", item = newItem.toTodoDto())
            )
        }

        return itemWrapper.item.toTodoItem()
    }

    override suspend fun deleteTodoItemById(todoId: String): TodoItem {
        val itemWrapper = makeRequestForItem {
            api.deleteTodoItem(
                revision = revision.get(),
                todoId
            )
        }

        return itemWrapper.item.toTodoItem()
    }

    private suspend fun makeRequestForList(call: suspend () -> Response<TodoListWrapper>): TodoListWrapper {
        val result = makeBaseRequest(call)

        updateRevisionIfPossible(result.revision)

        return result
    }

    private suspend fun makeRequestForItem(call: suspend () -> Response<TodoWrapper>): TodoWrapper {
        val result = makeBaseRequest(call)

        updateRevisionIfPossible(result.revision)

        return result
    }

    private suspend fun <T> makeBaseRequest(call: suspend () -> Response<T>): T = try {
        call.invoke().body()!!
    } catch (httpException: HttpException) {
        val code = httpException.code()
        throw when (code) {
            400 -> Exception("Данные с сервером не синхронизированы. Выполните синхронизацию и повторите попытку.")
            401 -> Exception("Возникли проблемы с авторизацией. Выполните авторизацию и повторите попытку.")
            404 -> Exception("Ваше дело не было найдено на сервере. Выполните синхронизацию и повторите попытку.")
            500 -> Exception("На сервере возникла внутренняя ошибка. Вы не виноваты. Повторите попытку позже.")
            else -> Exception("Возникла неизвестная ошибка во время выполнения запроса. Повторите попытку позже.")
        }
    } catch (socketException: SocketTimeoutException) {
        throw Exception("Запрос выполнялся слишком долго. Повторите попытку позже.")
    } catch (unknownHostException: UnknownHostException) {
        throw Exception("Проблемы с сетью или у вас отсутствует подключение к интернету. Повторите попытку позже.")
    } catch (ioException: IOException) {
        throw Exception("Возможно проблемы с сетью или соедиение нестабильно. Повторите ошибку позже.")
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        throw Exception("Неизвестная ошибка. Возможна ошибка в приложении или сервер прислал пустой ответ. Будем рады, если вы сообщите об ошибке.")
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
            lastDeviceId = identificationManager.getAndroidDeviceId(),
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
}