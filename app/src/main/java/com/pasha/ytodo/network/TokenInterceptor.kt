package com.pasha.ytodo.network

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = chain.request().newBuilder()

        when (token.type) {
            TokenType.Bearer -> {
                request.addHeader(AUTH_HEADER, "Bearer ${token.value}")
            }

            else -> {}
        }

        return chain.proceed(request.build())
    }

    companion object {
        private const val AUTH_HEADER: String = "Authorization"
    }
}