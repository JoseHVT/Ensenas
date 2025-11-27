package com.example.chat_bot.data.repository

import com.example.chat_bot.data.api.ApiService
import com.example.chat_bot.data.models.MemoryRunRequest
import com.example.chat_bot.data.models.MemoryRunResponse
import com.example.chat_bot.data.models.SignPairResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository para el juego de Memory (Memorama)
 * Maneja la obtención de mazos de cartas y el guardado de resultados
 */
class MemoryGameRepository(private val apiService: ApiService) {
    
    /**
     * Obtiene un mazo aleatorio de pares para el juego
     * @param size Número de pares (ej. 8 pares = 16 cartas)
     * @return Result con lista de SignPairResponse o error
     */
    suspend fun getMemoryDeck(size: Int = 8): Result<List<SignPairResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMemoryDeck(size)
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener mazo: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Envía los resultados de una partida de Memory al backend
     * @param token Token de autenticación
     * @param matches Número de pares encontrados
     * @param attempts Número de intentos totales
     * @param durationMs Duración en milisegundos
     * @param streak Racha actual (opcional)
     * @param moduleId ID del módulo asociado (opcional)
     * @return Result con MemoryRunResponse o error
     */
    suspend fun submitMemoryRun(
        token: String,
        matches: Int,
        attempts: Int,
        durationMs: Int,
        streak: Int? = null,
        moduleId: Int? = null
    ): Result<MemoryRunResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = MemoryRunRequest(
                    matches = matches,
                    attempts = attempts,
                    streak = streak,
                    durationMs = durationMs,
                    moduleId = moduleId
                )
                
                val response = apiService.submitMemoryRun("Bearer $token", request)
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al guardar resultado: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
