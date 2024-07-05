package com.pasha.ytodo.network

import com.pasha.ytodo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient(tokenInterceptor: Interceptor) {
    private val contentType = "application/json".toMediaType()
    private val kotlinConverterFactory = Json {
        ignoreUnknownKeys = true
    }.asConverterFactory(contentType)

    private val certificate = CertificatePinner.Builder()
        .add(BuildConfig.URL_PATTERN, BuildConfig.CERTIFICATE)
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
        .readTimeout(IO_TIMEOUT_SEC, TimeUnit.SECONDS)
        .writeTimeout(IO_TIMEOUT_SEC, TimeUnit.SECONDS)
        .connectionPool(
            ConnectionPool(
                maxIdleConnections = CONNECTIONS_NUM,
                keepAliveDuration = MINUTES_ALIVE_DURATION,
                TimeUnit.MINUTES
            )
        )
        .certificatePinner(certificate)
        .addInterceptor(tokenInterceptor)
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
        const val CONNECTION_TIMEOUT_SEC: Long = 20
        const val IO_TIMEOUT_SEC: Long = 25
        const val RETRY_TIMEOUT_MILLIS: Long = 3000
        const val MAX_RETRIES = 2
    }
}