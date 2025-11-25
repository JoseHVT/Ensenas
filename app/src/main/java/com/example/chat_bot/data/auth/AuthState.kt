package com.example.chat_bot.data.auth

/**
 * Estados de autenticación posibles
 */
sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
