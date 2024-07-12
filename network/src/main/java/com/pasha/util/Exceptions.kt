package com.pasha.util


sealed class RequestException(val code: Int, cause: Throwable? = null): RuntimeException(cause) {
    class BadRequest(cause: Throwable?) : RequestException(400, cause)
    class Unauthorized(cause: Throwable?) : RequestException(401, cause)
    class NotFound(cause: Throwable?) : RequestException(404, cause)
    class InternalServer(cause: Throwable?) : RequestException(500, cause)
    class Timeout(cause: Throwable?) : RequestException(0, cause)
    class Unknown(cause: Throwable?) : RequestException(0, cause)
    class NoConnection(cause: Throwable?) : RequestException(0, cause)
}