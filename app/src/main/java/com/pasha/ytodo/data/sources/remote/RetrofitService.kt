package com.pasha.ytodo.data.sources.remote

import android.content.Context
import com.pasha.ytodo.R
import com.pasha.ytodo.core.DeviceIdentificationManager
import com.pasha.ytodo.data.models.TodoDto
import com.pasha.ytodo.data.models.TodoListWrapper
import com.pasha.ytodo.data.models.TodoWrapper
import com.pasha.ytodo.domain.entities.TaskPriority
import com.pasha.ytodo.domain.entities.TaskProgress
import com.pasha.ytodo.domain.entities.TodoItem
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.network.NetworkClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.coroutineContext

class RetrofitService(
    private val api: TodoApi,
    private val identificationManager: DeviceIdentificationManager,
    private val context: Context
) : DataSource {
    private val revision: AtomicInteger = AtomicInteger(NO_REVISION)

    private fun updateRevisionIfPossible(revision: Int?) {
        if (revision != null) this.revision.set(revision)
    }

    override fun getRevision(): Int? {
        return if (revision.get() == NO_REVISION) null else revision.get()
    }

    override suspend fun getTodoList(): List<TodoItem> {
        val listWrapper = makeRequestForList { api.getTodoList() }

        return listWrapper.todos.map { it.toTodoItem() }
    }

    override suspend fun updateTodoList(newList: List<TodoItem>): List<TodoItem> {
        val list = newList.map { it.toTodoDto() }

        println(list)

        val listWrapper = makeRequestForList {
            api.updateTodoList(
                revision = revision.get(),
                content = TodoListWrapper(status = "ok", todos = list)
            )
        }

        println(listWrapper.todos)

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
        tryRetryingRequests(
            NetworkClient.MAX_RETRIES,
            NetworkClient.RETRY_TIMEOUT_MILLIS,
            call
        ).body()!!
    } catch (httpException: HttpException) {
        val code = httpException.code()
        throw when (code) {
            400 -> Exception(context.getString(R.string.network_error_400))
            401 -> Exception(context.getString(R.string.network_error_401))
            404 -> Exception(context.getString(R.string.network_error_404))
            500 -> Exception(context.getString(R.string.network_error_500))
            else -> Exception(context.getString(R.string.network_error_unknown))
        }
    } catch (socketException: SocketTimeoutException) {
        throw Exception()
    } catch (unknownHostException: UnknownHostException) {
        throw Exception(context.getString(R.string.network_error_connection))
    } catch (ioException: IOException) {
        throw Exception(context.getString(R.string.network_error_unstable))
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        
        throw Exception(context.getString(R.string.unknown_error))
    }

    private suspend fun <T> tryRetryingRequests(
        maxRetries: Int,
        interval: Long,
        call: suspend () -> Response<T>
    ): Response<T> {
        repeat(maxRetries) { numTry ->
            try {
                val response = withTimeout(interval * numTry) {
                    call.invoke()
                }

                if (response.isSuccessful) return response
            } catch (httpException: HttpException) {
                when (httpException.code()) {
                    500 -> {}
                    else -> throw httpException
                }
            } catch (_: SocketTimeoutException) {

            } catch (_: TimeoutCancellationException) {  }
        }

        throw SocketTimeoutException()
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

    companion object {
        private const val NO_REVISION = -1
    }
}