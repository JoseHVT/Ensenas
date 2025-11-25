package com.example.chat_bot.data.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor que añade automáticamente el token de autenticación
 * a todas las peticiones HTTP que requieren autenticación
 */
class AuthInterceptor(
    private val authRepository: AuthRepository
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Si la petición ya tiene un header de Authorization, no lo modificamos
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }
        
        // Obtener el token de Firebase (bloqueante porque OkHttp Interceptor no es suspendible)
        val token = runBlocking {
            authRepository.getAuthToken()
        }
        
        // Si no hay token, procede sin modificar la petición
        if (token == null) {
            return chain.proceed(originalRequest)
        }
        
        // Añade el token al header Authorization
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
}
