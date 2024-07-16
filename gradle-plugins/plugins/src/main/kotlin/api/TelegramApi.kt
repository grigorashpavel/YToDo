package api

import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.internal.impldep.org.apache.http.HttpResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TelegramApi {
    @Multipart
    @POST("/bot{token}/sendDocument")
    suspend fun sendFile(
        @Header("Content-Disposition") contentDisposition: String,
        @Path("token") token: String,
        @Query("chat_id") chatId: String,
        @Part document: MultipartBody.Part,
    )

    @POST("/bot{token}/sendMessage")
    suspend fun sendMessage(
        @Path("token") token: String,
        @Query("chat_id") chatId: String,
        @Query("text") message: String,
    )

    @GET("/bot{token}/getMe")
    fun getBotInfo(
        @Path("token") token: String
    ): Call<BotInfoResponse>
}