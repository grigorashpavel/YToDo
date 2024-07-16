package api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File


class NetworkClient {
    private val contentType = "application/json".toMediaType()
    private val kotlinConverterFactory = Json {
        ignoreUnknownKeys = true
    }.asConverterFactory(contentType)

    private val okHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(kotlinConverterFactory)
            .baseUrl("https://api.telegram.org/")
            .build()
    }

    private val api by lazy {
        retrofit.create(TelegramApi::class.java)
    }

    suspend fun sendFile(file: File, token: String, chatId: String) {
        val filePart = MultipartBody.Part.createFormData(
            "document",
            file.name,
            file.asRequestBody("multipart/form-data".toMediaType())
        )
        api.sendFile(
            contentDisposition = "filename=${file.name}",
            token = token,
            chatId = chatId,
            document = filePart
        )
    }

    suspend fun sendMessage(message: String, token: String, chatId: String) {
        api.sendMessage(
            chatId = chatId,
            token = token,
            message = message
        )
    }

    suspend fun pingBot(token: String): BotInfoResponse {
        return api.getBotInfo(token).await()
    }
}