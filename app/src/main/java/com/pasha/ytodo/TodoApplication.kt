package com.pasha.ytodo

import android.app.Application
import androidx.compose.animation.ContentTransform
import androidx.core.view.accessibility.AccessibilityEventCompat.ContentChangeType
import com.pasha.ytodo.data.repositories.TodoItemsRepositoryImpl
import com.pasha.ytodo.data.sources.local.LocalTodos
import com.pasha.ytodo.data.sources.remote.TodoApi
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import com.pasha.ytodo.network.NetworkClient
import com.pasha.ytodo.network.Token
import com.pasha.ytodo.network.TokenInterceptor
import com.pasha.ytodo.network.TokenManager
import com.pasha.ytodo.network.TokenType
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class TodoApplication : Application(), TodoItemRepositoryProvider {
    private val manager = object : TokenManager {
        override fun getToken(): Token {
            return Token(BuildConfig.TOKEN_BEARER, TokenType.Bearer)
        }
    }
    private val tokenInterceptor: Interceptor = TokenInterceptor(manager)
    val client = NetworkClient(tokenInterceptor)

    private val local = LocalTodos()
    val todoService = client.createRetrofitService(TodoApi::class.java)

    override val todoItemsRepository: TodoItemsRepository =
        TodoItemsRepositoryImpl(local, todoService)
}