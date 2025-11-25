package com.example.chat_bot.data.api

import com.example.chat_bot.data.auth.AuthInterceptor
import com.example.chat_bot.data.auth.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    
    // Para emulador Android: 10.0.2.2 = localhost de tu PC
    // Para dispositivo físico: usa la IP local de tu PC (ej: 192.168.1.X)
    private const val BASE_URL = "http://10.0.2.2:8000/"
    
    // AuthRepository para obtener tokens
    private val authRepository = AuthRepository()
    
    // Logging interceptor para debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Auth interceptor para añadir tokens automáticamente
    private val authInterceptor = AuthInterceptor(authRepository)
    
    // OkHttp client con configuración de timeouts y auth
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Primero auth
        .addInterceptor(loggingInterceptor) // Luego logging
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Retrofit instance (singleton)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Service instance
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
