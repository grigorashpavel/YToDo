package com.pasha.util

import com.pasha.models.TodoListWrapper
import com.pasha.models.TodoWrapper
import com.pasha.network.NetworkClient
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


suspend fun makeRequestForList(call: suspend () -> Response<TodoListWrapper>): TodoListWrapper {
    val result = makeBaseRequest(call)

    return result
}

suspend fun makeRequestForItem(call: suspend () -> Response<TodoWrapper>): TodoWrapper {
    val result = makeBaseRequest(call)

    return result
}

suspend fun <T> makeBaseRequest(call: suspend () -> Response<T>): T = try {
    val a = tryRetryingRequests(
        NetworkClient.MAX_RETRIES,
        NetworkClient.RETRY_TIMEOUT_MILLIS,
        call
    )
    a.body()!!
} catch (httpException: HttpException) {
    val code = httpException.code()
    throw when (code) {
        400 -> RequestException.BadRequest(httpException.cause)
        401 -> RequestException.Unauthorized(httpException.cause)
        404 -> RequestException.NotFound(httpException.cause)
        500 -> RequestException.InternalServer(httpException.cause)
        else -> RequestException.Unknown(httpException.cause)
    }
} catch (socketException: SocketTimeoutException) {
    throw RequestException.Timeout(socketException.cause)
} catch (unknownHostException: UnknownHostException) {
    throw RequestException.NoConnection(unknownHostException.cause)
} catch (ioException: IOException) {
    throw RequestException.NoConnection(ioException.cause)
} catch (e: Exception) {
    throw e
}

private suspend fun <T> tryRetryingRequests(
    maxRetries: Int,
    interval: Long,
    call: suspend () -> Response<T>
): Response<T> {
    repeat(maxRetries) { numTry ->
        try {
            val response = withTimeout(interval * (numTry + 1)) {
                call.invoke()
            }

            if (response.isSuccessful) return response
        } catch (httpException: HttpException) {
            httpException.printStackTrace()
            when (httpException.code()) {
                500 -> {}
                else -> throw httpException
            }
        } catch (_: SocketTimeoutException) {

        } catch (_: TimeoutCancellationException) {

        }
    }

    throw SocketTimeoutException()
}
