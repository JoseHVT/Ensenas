package com.example.chat_bot.data.models

import com.google.gson.annotations.SerializedName

// Response models que coinciden con el backend FastAPI

data class SignResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("word") val word: String,
    @SerializedName("category") val category: String,
    @SerializedName("video_path") val videoPath: String,
    @SerializedName("thumb_path") val thumbPath: String?,
    @SerializedName("tags") val tags: String? // JSON string
)

data class SignDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("word") val word: String,
    @SerializedName("category") val category: String,
    @SerializedName("video_path") val videoPath: String,
    @SerializedName("thumb_path") val thumbPath: String?,
    @SerializedName("tags") val tags: String?,
    @SerializedName("definition") val definition: String = "",
    @SerializedName("examples") val examples: List<String> = emptyList(),
    @SerializedName("related_signs") val relatedSigns: List<String> = emptyList()
)

data class ModuleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("sort_order") val sortOrder: Int,
    @SerializedName("created_at") val createdAt: String
)

data class UserResponse(
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String?,
    @SerializedName("created_at") val createdAt: String
)

// Pagination response
data class PaginatedSignsResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("items") val items: List<SignResponse>
)

// ===== QUIZ MODELS =====

data class QuizResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("module_id") val moduleId: Int,
    @SerializedName("type") val type: String, // 'multiple_choice', 'complete', 'pair'
    @SerializedName("title") val title: String,
    @SerializedName("questions") val questions: List<QuizQuestionResponse> = emptyList()
)

data class QuizQuestionResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("quiz_id") val quizId: Int,
    @SerializedName("prompt") val prompt: String,
    @SerializedName("options") val options: Map<String, String>?, // JSON object
    @SerializedName("answer") val answer: String?
)

data class QuizAttemptRequest(
    @SerializedName("quiz_id") val quizId: Int,
    @SerializedName("answers") val answers: Map<String, String>, // question_id: answer
    @SerializedName("score") val score: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("duration_ms") val durationMs: Int?
)

data class QuizAttemptResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("quiz_id") val quizId: Int,
    @SerializedName("score") val score: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("duration_ms") val durationMs: Int?,
    @SerializedName("created_at") val createdAt: String
)

// ===== MEMORY GAME MODELS =====

data class SignPairResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("word") val word: String,
    @SerializedName("sign_id") val signId: Int,
    @SerializedName("sign") val sign: SignResponse
)

data class MemoryRunRequest(
    @SerializedName("matches") val matches: Int,
    @SerializedName("attempts") val attempts: Int,
    @SerializedName("streak") val streak: Int?,
    @SerializedName("duration_ms") val durationMs: Int,
    @SerializedName("module_id") val moduleId: Int?
)

data class MemoryRunResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("matches") val matches: Int,
    @SerializedName("attempts") val attempts: Int,
    @SerializedName("streak") val streak: Int?,
    @SerializedName("duration_ms") val durationMs: Int,
    @SerializedName("module_id") val moduleId: Int?,
    @SerializedName("created_at") val createdAt: String
)

// ===== PROGRESS MODELS =====

data class UserProgressRequest(
    @SerializedName("module_id") val moduleId: Int,
    @SerializedName("percent") val percent: Int // 0-100
)

data class UserProgressResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("module_id") val moduleId: Int,
    @SerializedName("percent") val percent: Int,
    @SerializedName("last_activity") val lastActivity: String
)

data class StatsResponse(
    @SerializedName("precision_global") val precisionGlobal: Float,
    @SerializedName("tiempo_total_ms") val tiempoTotalMs: Int,
    @SerializedName("racha_actual") val rachaActual: Int,
    @SerializedName("senas_dominadas") val senasDominadas: Int
)
