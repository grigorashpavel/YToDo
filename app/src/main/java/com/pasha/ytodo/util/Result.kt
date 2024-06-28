package com.pasha.ytodo.util

import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat.MessagingStyle.Message


sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<out T>(val data: T) : Result<T>
    data class Failure(val error: Error) : Result<Nothing>
}

data class Error(val message: String)
