package com.pasha.ytodo

import android.app.Application
import com.pasha.ytodo.core.DeviceIdentificationManager
import com.pasha.ytodo.core.DeviceIdentificationManagerImpl
import com.pasha.ytodo.data.repositories.TodoItemsRepositoryImpl
import com.pasha.ytodo.data.sources.local.LocalTodos
import com.pasha.ytodo.data.sources.remote.RetrofitService
import com.pasha.ytodo.data.sources.remote.TodoApi
import com.pasha.ytodo.domain.repositories.TodoItemRepositoryProvider
import com.pasha.ytodo.domain.repositories.TodoItemsRepository
import com.pasha.ytodo.network.NetworkClient
import com.pasha.ytodo.domain.DataSource
import com.pasha.ytodo.network.Token
import com.pasha.ytodo.network.TokenInterceptor
import com.pasha.ytodo.network.TokenManager
import com.pasha.ytodo.network.TokenType
import okhttp3.Interceptor

class TodoApplication : Application(), TodoItemRepositoryProvider {
    private val manager = object : TokenManager {
        override fun getToken(): Token {
            return Token(BuildConfig.TOKEN_BEARER, TokenType.Bearer)
        }
    }
    private val tokenInterceptor: Interceptor = TokenInterceptor(manager)
    val client = NetworkClient(tokenInterceptor)

    val todoApi = client.createRetrofitService(TodoApi::class.java)
    private val todoService: DataSource by lazy {
        RetrofitService(todoApi, identificationManager)
    }

    private val identificationManager: DeviceIdentificationManager by lazy {
        DeviceIdentificationManagerImpl(applicationContext)
    }

    private val local = LocalTodos()
    override val todoItemsRepository: TodoItemsRepository by lazy {
        TodoItemsRepositoryImpl(local, todoService)
    }
}