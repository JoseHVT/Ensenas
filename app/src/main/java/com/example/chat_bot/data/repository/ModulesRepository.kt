package com.example.chat_bot.data.repository

import com.example.chat_bot.data.api.ApiService
import com.example.chat_bot.data.models.ModuleResponse
import com.example.chat_bot.data.models.UserProgressRequest
import com.example.chat_bot.data.models.UserProgressResponse
import retrofit2.Response

/**
 * Repository interface for Modules operations
 * Enables dependency injection and testing
 */
interface ModulesRepository {
    suspend fun getModules(): Response<List<ModuleResponse>>
    suspend fun updateProgress(token: String, progress: UserProgressRequest): Response<UserProgressResponse>
}

/**
 * Implementation of ModulesRepository using ApiService
 */
class ModulesRepositoryImpl(
    private val api: ApiService
) : ModulesRepository {
    
    override suspend fun getModules(): Response<List<ModuleResponse>> {
        return api.getModules()
    }
    
    override suspend fun updateProgress(
        token: String,
        progress: UserProgressRequest
    ): Response<UserProgressResponse> {
        return api.updateProgress(token, progress)
    }
}
