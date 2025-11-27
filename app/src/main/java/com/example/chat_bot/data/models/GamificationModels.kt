package com.example.chat_bot.data.models

import com.google.gson.annotations.SerializedName

/**
 * Sistema de Niveles 1-50
 * Fórmula exponencial: XP_Requerido = 100 * (nivel - 1)^1.5
 */
data class UserLevel(
    val level: Int,
    val currentXP: Int,
    val requiredXP: Int,
    val progress: Float // 0.0 - 1.0
) {
    companion object {
        fun calculateLevel(totalXP: Int): UserLevel {
            var level = 1
            var xpForCurrentLevel = 0
            var xpForNextLevel = 0
            
            // Niveles 1-50 con curva exponencial
            for (i in 1..50) {
                xpForNextLevel = calculateXPForLevel(i + 1)
                if (totalXP < xpForNextLevel) {
                    level = i
                    xpForCurrentLevel = calculateXPForLevel(i)
                    break
                }
            }
            
            // Si tiene más XP que el nivel 50, se queda en 50
            if (level == 1 && totalXP >= xpForNextLevel) {
                level = 50
                xpForCurrentLevel = calculateXPForLevel(50)
                xpForNextLevel = calculateXPForLevel(51)
            }
            
            val currentLevelXP = totalXP - xpForCurrentLevel
            val requiredXP = xpForNextLevel - xpForCurrentLevel
            val progress = if (level == 50) 1f else (currentLevelXP.toFloat() / requiredXP.toFloat())
            
            return UserLevel(
                level = level,
                currentXP = currentLevelXP,
                requiredXP = requiredXP,
                progress = progress.coerceIn(0f, 1f)
            )
        }
        
        private fun calculateXPForLevel(level: Int): Int {
            if (level == 1) return 0
            // Nivel 2: 100, Nivel 3: 250, Nivel 10: 2000, Nivel 50: 100000
            return (100 * Math.pow((level - 1).toDouble(), 1.5)).toInt()
        }
        
        fun getLevelTitle(level: Int): String = when {
            level < 5 -> "Aprendiz"
            level < 10 -> "Estudiante"
            level < 20 -> "Practicante"
            level < 30 -> "Comunicador"
            level < 40 -> "Experto"
            level < 50 -> "Maestro"
            else -> "Leyenda LSM"
        }
    }
}

/**
 * Achievement (Logro)
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji o resource name
    val category: AchievementCategory,
    val requirement: Int, // Valor necesario para desbloquear
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val progress: Int = 0, // Progreso actual hacia el logro
    val unlockedAt: String? = null
)

enum class AchievementCategory {
    LECCIONES,      // Completar lecciones
    RACHA,          // Mantener racha
    PRECISION,      // Precisión en quizzes
    VELOCIDAD,      // Completar rápido
    MAESTRIA,       // Dominar módulos
    SOCIAL,         // Interactuar con otros
    ESPECIAL        // Eventos especiales
}

/**
 * Logros predefinidos (25 achievements)
 */
object Achievements {
    val ALL = listOf(
        // LECCIONES (5)
        Achievement(
            id = "primera_leccion",
            title = "Primera Lección",
            description = "Completa tu primera lección",
            icon = "🎓",
            category = AchievementCategory.LECCIONES,
            requirement = 1,
            xpReward = 50
        ),
        Achievement(
            id = "leccion_10",
            title = "Dedicado",
            description = "Completa 10 lecciones",
            icon = "📚",
            category = AchievementCategory.LECCIONES,
            requirement = 10,
            xpReward = 100
        ),
        Achievement(
            id = "leccion_50",
            title = "Estudioso",
            description = "Completa 50 lecciones",
            icon = "📖",
            category = AchievementCategory.LECCIONES,
            requirement = 50,
            xpReward = 250
        ),
        Achievement(
            id = "leccion_100",
            title = "Sabio",
            description = "Completa 100 lecciones",
            icon = "🎖️",
            category = AchievementCategory.LECCIONES,
            requirement = 100,
            xpReward = 500
        ),
        Achievement(
            id = "todos_modulos",
            title = "Maestro LSM",
            description = "Completa todos los módulos",
            icon = "👑",
            category = AchievementCategory.LECCIONES,
            requirement = 8, // 8 módulos
            xpReward = 1000
        ),
        
        // RACHA (5)
        Achievement(
            id = "racha_3",
            title = "Constante",
            description = "Mantén una racha de 3 días",
            icon = "🔥",
            category = AchievementCategory.RACHA,
            requirement = 3,
            xpReward = 75
        ),
        Achievement(
            id = "racha_7",
            title = "Comprometido",
            description = "Mantén una racha de 7 días",
            icon = "🔥🔥",
            category = AchievementCategory.RACHA,
            requirement = 7,
            xpReward = 150
        ),
        Achievement(
            id = "racha_30",
            title = "Imparable",
            description = "Mantén una racha de 30 días",
            icon = "🔥🔥🔥",
            category = AchievementCategory.RACHA,
            requirement = 30,
            xpReward = 500
        ),
        Achievement(
            id = "racha_100",
            title = "Leyenda",
            description = "Mantén una racha de 100 días",
            icon = "⚡",
            category = AchievementCategory.RACHA,
            requirement = 100,
            xpReward = 2000
        ),
        Achievement(
            id = "racha_365",
            title = "Año Perfecto",
            description = "Mantén una racha de 365 días",
            icon = "💎",
            category = AchievementCategory.RACHA,
            requirement = 365,
            xpReward = 5000
        ),
        
        // PRECISIÓN (5)
        Achievement(
            id = "quiz_perfecto",
            title = "Perfeccionista",
            description = "Completa un quiz sin errores",
            icon = "✨",
            category = AchievementCategory.PRECISION,
            requirement = 1,
            xpReward = 100
        ),
        Achievement(
            id = "quiz_perfecto_10",
            title = "Experto",
            description = "Completa 10 quizzes perfectos",
            icon = "⭐",
            category = AchievementCategory.PRECISION,
            requirement = 10,
            xpReward = 300
        ),
        Achievement(
            id = "precision_90",
            title = "Certero",
            description = "Alcanza 90% de precisión global",
            icon = "🎯",
            category = AchievementCategory.PRECISION,
            requirement = 90,
            xpReward = 400
        ),
        Achievement(
            id = "sin_vidas_perdidas",
            title = "Corazón Intacto",
            description = "Completa 5 quizzes con 3 corazones",
            icon = "💚",
            category = AchievementCategory.PRECISION,
            requirement = 5,
            xpReward = 250
        ),
        Achievement(
            id = "precision_100",
            title = "Infalible",
            description = "Alcanza 100% de precisión en un módulo",
            icon = "💯",
            category = AchievementCategory.PRECISION,
            requirement = 100,
            xpReward = 600
        ),
        
        // VELOCIDAD (5)
        Achievement(
            id = "speed_round_oro",
            title = "Velocista",
            description = "Completa Speed Round con más de 25s",
            icon = "⚡",
            category = AchievementCategory.VELOCIDAD,
            requirement = 25,
            xpReward = 150
        ),
        Achievement(
            id = "memory_rapido",
            title = "Memoria Rápida",
            description = "Completa Memory Game en menos de 2 min",
            icon = "🧠",
            category = AchievementCategory.VELOCIDAD,
            requirement = 120, // segundos
            xpReward = 200
        ),
        Achievement(
            id = "leccion_rapida",
            title = "Rayo LSM",
            description = "Completa 3 lecciones en 1 hora",
            icon = "⚡⚡",
            category = AchievementCategory.VELOCIDAD,
            requirement = 3,
            xpReward = 250
        ),
        Achievement(
            id = "quiz_10_min",
            title = "Eficiente",
            description = "Completa un quiz en menos de 10 min",
            icon = "⏱️",
            category = AchievementCategory.VELOCIDAD,
            requirement = 600, // segundos
            xpReward = 180
        ),
        Achievement(
            id = "dia_completo",
            title = "Maratón LSM",
            description = "Gana 500 XP en un solo día",
            icon = "🏃",
            category = AchievementCategory.VELOCIDAD,
            requirement = 500,
            xpReward = 300
        ),
        
        // MAESTRÍA (3)
        Achievement(
            id = "modulo_completado",
            title = "Módulo Dominado",
            description = "Completa un módulo al 100%",
            icon = "🎯",
            category = AchievementCategory.MAESTRIA,
            requirement = 1,
            xpReward = 200
        ),
        Achievement(
            id = "senas_50",
            title = "Vocabulario Rico",
            description = "Domina 50 señas diferentes",
            icon = "🗣️",
            category = AchievementCategory.MAESTRIA,
            requirement = 50,
            xpReward = 350
        ),
        Achievement(
            id = "senas_200",
            title = "Diccionario Viviente",
            description = "Domina 200 señas diferentes",
            icon = "📕",
            category = AchievementCategory.MAESTRIA,
            requirement = 200,
            xpReward = 1000
        ),
        
        // ESPECIAL (2)
        Achievement(
            id = "primer_dia",
            title = "¡Bienvenido!",
            description = "Completa tu primer día en EnSeñas",
            icon = "👋",
            category = AchievementCategory.ESPECIAL,
            requirement = 1,
            xpReward = 25
        ),
        Achievement(
            id = "nivel_50",
            title = "Leyenda LSM",
            description = "Alcanza el nivel 50",
            icon = "🏆",
            category = AchievementCategory.ESPECIAL,
            requirement = 50,
            xpReward = 5000
        )
    )
    
    fun getById(id: String): Achievement? = ALL.find { it.id == id }
    
    fun getByCategory(category: AchievementCategory): List<Achievement> = 
        ALL.filter { it.category == category }
}

/**
 * Streak (Racha)
 */
data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String, // ISO 8601 date
    val isActiveToday: Boolean
) {
    companion object {
        fun calculateStreak(lastActivity: String, todayActivity: Boolean): Int {
            // TODO: Implementar lógica de cálculo de racha basado en fechas
            // Por ahora retorna mock data
            return if (todayActivity) 7 else 6
        }
        
        fun shouldResetStreak(lastActivity: String, currentDate: String): Boolean {
            // TODO: Comparar fechas - si pasó más de 1 día, reset
            return false
        }
    }
}

/**
 * Leaderboard Entry
 */
data class LeaderboardEntry(
    @SerializedName("user_id") val userId: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("total_xp") val totalXP: Int,
    @SerializedName("weekly_xp") val weeklyXP: Int? = null,
    val level: Int,
    val rank: Int,
    @SerializedName("is_friend") val isFriend: Boolean = false
)

/**
 * Leaderboard Response
 */
data class LeaderboardResponse(
    @SerializedName("leaderboard_type") val leaderboardType: String, // "weekly", "all_time", "friends"
    val entries: List<LeaderboardEntry>,
    @SerializedName("user_rank") val userRank: Int,
    @SerializedName("total_users") val totalUsers: Int
)

/**
 * Daily Goal
 */
data class DailyGoal(
    val targetXP: Int = 50, // Meta diaria por defecto: 50 XP
    val currentXP: Int,
    val isCompleted: Boolean,
    val progress: Float // 0.0 - 1.0
) {
    companion object {
        fun fromXP(currentXP: Int, target: Int = 50): DailyGoal {
            val progress = (currentXP.toFloat() / target.toFloat()).coerceIn(0f, 1f)
            return DailyGoal(
                targetXP = target,
                currentXP = currentXP,
                isCompleted = currentXP >= target,
                progress = progress
            )
        }
    }
}

/**
 * Achievement Notification (para mostrar popup cuando se desbloquea)
 */
data class AchievementNotification(
    val achievement: Achievement,
    val timestamp: Long = System.currentTimeMillis(),
    var isShown: Boolean = false
)

/**
 * Streak Info - Información de racha del usuario
 */
data class StreakInfo(
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("longest_streak") val longestStreak: Int,
    @SerializedName("last_activity_date") val lastActivityDate: String?,
    @SerializedName("weekly_calendar") val weeklyCalendar: List<Boolean>, // 7 días
    @SerializedName("total_active_days") val totalActiveDays: Int
)

/**
 * Daily Activity - Actividad diaria del usuario
 */
data class DailyActivity(
    val id: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("activity_date") val activityDate: String,
    @SerializedName("quizzes_completed") val quizzesCompleted: Int,
    @SerializedName("lessons_completed") val lessonsCompleted: Int,
    @SerializedName("memory_games_completed") val memoryGamesCompleted: Int,
    @SerializedName("xp_earned") val xpEarned: Int,
    @SerializedName("created_at") val createdAt: String
)

/**
 * Streak Update Request
 */
data class StreakUpdateRequest(
    @SerializedName("activity_type") val activityType: String, // "quiz", "lesson", "memory_game"
    @SerializedName("xp_earned") val xpEarned: Int = 0
)

/**
 * XP Transaction Response - Historial de XP ganado
 */
data class XPTransactionResponse(
    val id: Int,
    @SerializedName("user_id") val userId: String,
    val amount: Int,
    val source: String, // "quiz", "memory_game", "lesson", "streak_bonus", "achievement"
    @SerializedName("source_id") val sourceId: Int?,
    val description: String?,
    @SerializedName("created_at") val createdAt: String
)

/**
 * User Level Info Response - Información de nivel del usuario
 */
data class UserLevelInfoResponse(
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("current_level") val currentLevel: Int,
    @SerializedName("level_title") val levelTitle: String,
    @SerializedName("xp_for_current_level") val xpForCurrentLevel: Int,
    @SerializedName("xp_for_next_level") val xpForNextLevel: Int,
    @SerializedName("current_level_xp") val currentLevelXp: Int,
    @SerializedName("required_xp") val requiredXp: Int,
    val progress: Float
)

/**
 * XP Award Request - Request para otorgar XP
 */
data class XPAwardRequest(
    val amount: Int,
    val source: String,
    @SerializedName("source_id") val sourceId: Int? = null,
    val description: String? = null
)

/**
 * XP Award Response - Response al otorgar XP
 */
data class XPAwardResponse(
    @SerializedName("xp_awarded") val xpAwarded: Int,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("previous_level") val previousLevel: Int,
    @SerializedName("current_level") val currentLevel: Int,
    @SerializedName("level_up") val levelUp: Boolean,
    @SerializedName("level_info") val levelInfo: UserLevelInfoResponse
)

