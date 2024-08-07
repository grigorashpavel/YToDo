package com.pasha.network

interface TokenManager {
    fun getToken(): Token
}

data class Token(val value: String, val type: TokenType)

enum class TokenType {
    OAuth, Bearer
}