package com.example.chat_bot.data.api

import com.example.chat_bot.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ===== DICTIONARY ENDPOINTS =====
    
    @GET("dictionary")
    suspend fun getSigns(
        @Query("query") search: String? = null,
        @Query("category") category: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<SignResponse>>
    
    @GET("dictionary/{word}")
    suspend fun getSignDetail(
        @Path("word") word: String
    ): Response<SignDetailResponse>
    
    // ===== MODULES ENDPOINTS =====
    
    @GET("modules")
    suspend fun getModules(): Response<List<ModuleResponse>>
    
    @GET("modules/{module_id}")
    suspend fun getModuleById(
        @Path("module_id") moduleId: Int
    ): Response<ModuleResponse>
    
    // ===== USER ENDPOINTS =====
    
    @GET("users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>
    
    // ===== QUIZ ENDPOINTS =====
    
    @GET("quizzes")
    suspend fun getQuizzesForModule(
        @Query("module_id") moduleId: Int
    ): Response<List<QuizResponse>>
    
    @GET("quizzes/{quiz_id}")
    suspend fun getQuizDetails(
        @Path("quiz_id") quizId: Int
    ): Response<QuizResponse>
    
    @POST("quizzes/attempt")
    suspend fun submitQuizAttempt(
        @Header("Authorization") token: String,
        @Body attempt: QuizAttemptRequest
    ): Response<QuizAttemptResponse>
    
    @GET("quizzes/my-attempts")
    suspend fun getMyQuizAttempts(
        @Header("Authorization") token: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): Response<List<QuizAttemptResponse>>
    
    // ===== MEMORY GAME ENDPOINTS =====
    
    @GET("memory/deck")
    suspend fun getMemoryDeck(
        @Query("size") size: Int = 8
    ): Response<List<SignPairResponse>>
    
    @POST("memory/attempt")
    suspend fun submitMemoryRun(
        @Header("Authorization") token: String,
        @Body run: MemoryRunRequest
    ): Response<MemoryRunResponse>
    
    // ===== PROGRESS ENDPOINTS =====
    
    @POST("progress")
    suspend fun updateProgress(
        @Header("Authorization") token: String,
        @Body progress: UserProgressRequest
    ): Response<UserProgressResponse>
    
    @GET("progress")
    suspend fun getUserProgress(
        @Header("Authorization") token: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): Response<List<UserProgressResponse>>
    
    @GET("stats/summary")
    suspend fun getUserStats(
        @Header("Authorization") token: String
    ): Response<StatsResponse>
    
    // ===== STREAK ENDPOINTS =====
    
    @GET("streak")
    suspend fun getStreakInfo(
        @Header("Authorization") token: String
    ): Response<StreakInfo>
    
    @POST("streak/update")
    suspend fun updateStreak(
        @Header("Authorization") token: String,
        @Body update: StreakUpdateRequest
    ): Response<DailyActivity>
    
    // ===== XP AND LEVELS ENDPOINTS =====
    
    @GET("xp/level")
    suspend fun getUserLevelInfo(
        @Header("Authorization") token: String
    ): Response<UserLevelInfoResponse>
    
    @POST("xp/award")
    suspend fun awardXP(
        @Header("Authorization") token: String,
        @Body request: XPAwardRequest
    ): Response<XPAwardResponse>
    
    @GET("xp/transactions")
    suspend fun getXPTransactions(
        @Header("Authorization") token: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): Response<List<XPTransactionResponse>>
}
