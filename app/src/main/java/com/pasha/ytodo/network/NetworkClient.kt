package com.pasha.ytodo.network

import com.pasha.ytodo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class NetworkClient(tokenInterceptor: Interceptor) {
    private val contentType = "application/json".toMediaType()
    private val kotlinConverterFactory = Json.asConverterFactory(contentType)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(IO_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(IO_TIMEOUT, TimeUnit.SECONDS)
        .connectionPool(
            ConnectionPool(
                maxIdleConnections = CONNECTIONS_NUM,
                keepAliveDuration = MINUTES_ALIVE_DURATION,
                TimeUnit.MINUTES
            )
        )
        .addNetworkInterceptor(tokenInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(kotlinConverterFactory)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    fun <T> createRetrofitService(clazz: Class<T>): T = retrofit.create(clazz)

    companion object {
        const val MINUTES_ALIVE_DURATION: Long = 5
        const val CONNECTIONS_NUM: Int = 3
        const val CONNECTION_TIMEOUT: Long = 20
        const val IO_TIMEOUT: Long = 25
    }
}