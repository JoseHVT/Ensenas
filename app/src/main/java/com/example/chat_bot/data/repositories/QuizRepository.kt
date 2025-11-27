package com.example.chat_bot.data.repositories

import com.example.chat_bot.data.models.QuizAttemptRequest
import com.example.chat_bot.data.models.QuizAttemptResponse
import com.example.chat_bot.data.models.QuizResponse
import retrofit2.Response

/**
 * Repository interface for Quiz operations
 * Enables testing by abstracting API calls
 */
interface QuizRepository {
    suspend fun getQuizDetails(quizId: Int): Response<QuizResponse>
    suspend fun submitQuizAttempt(token: String, attempt: QuizAttemptRequest): Response<QuizAttemptResponse>
}

/**
 * Default implementation using RetrofitInstance
 */
class QuizRepositoryImpl(
    private val apiService: com.example.chat_bot.data.api.ApiService
) : QuizRepository {
    
    override suspend fun getQuizDetails(quizId: Int): Response<QuizResponse> {
        return apiService.getQuizDetails(quizId)
    }
    
    override suspend fun submitQuizAttempt(
        token: String,
        attempt: QuizAttemptRequest
    ): Response<QuizAttemptResponse> {
        return apiService.submitQuizAttempt(token, attempt)
    }
}
