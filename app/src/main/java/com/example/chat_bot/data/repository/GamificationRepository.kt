package com.example.chat_bot.data.repository

import com.example.chat_bot.data.api.ApiService
import com.example.chat_bot.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Gamification Repository - Maneja XP, racha, achievements, leaderboards
 */
class GamificationRepository(
    private val apiService: ApiService
) {
    // StateFlows para reactive updates
    private val _userLevel = MutableStateFlow<UserLevel?>(null)
    val userLevel: StateFlow<UserLevel?> = _userLevel.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    private val _pendingNotifications = MutableStateFlow<List<AchievementNotification>>(emptyList())
    val pendingNotifications: StateFlow<List<AchievementNotification>> = _pendingNotifications.asStateFlow()
    
    private val _dailyGoal = MutableStateFlow<DailyGoal?>(null)
    val dailyGoal: StateFlow<DailyGoal?> = _dailyGoal.asStateFlow()
    
    // Cache local
    private var cachedStats: StatsResponse? = null
    private var cachedProgress: List<UserProgressResponse> = emptyList()
    private var totalXP: Int = 0
    private var totalLessonsCompleted: Int = 0
    private var perfectQuizzes: Int = 0
    private var quizzesWithFullHearts: Int = 0
    
    /**
     * Inicializar datos de gamificación del usuario
     */
    suspend fun initializeGamificationData(token: String): Result<Unit> {
        return try {
            // TODO: Descomentar cuando el backend esté listo
            // val statsResponse = apiService.getStats("Bearer $token")
            // val progressResponse = apiService.getProgress("Bearer $token")
            
            // MOCK DATA temporal
            totalXP = 245 // Mock XP inicial
            totalLessonsCompleted = 29
            perfectQuizzes = 5
            quizzesWithFullHearts = 3
            
            // Calcular nivel
            _userLevel.value = UserLevel.calculateLevel(totalXP)
            
            // Calcular racha (mock)
            _currentStreak.value = 7
            
            // Calcular achievements
            updateAchievements()
            
            // Daily goal
            val todayXP = 0 // TODO: Calcular XP ganado hoy desde backend
            _dailyGoal.value = DailyGoal.fromXP(todayXP, 50)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Agregar XP al usuario
     */
    suspend fun addXP(xpAmount: Int, token: String) {
        totalXP += xpAmount
        
        // Actualizar nivel
        val oldLevel = _userLevel.value?.level ?: 1
        val newLevel = UserLevel.calculateLevel(totalXP)
        _userLevel.value = newLevel
        
        // Si subió de nivel, crear notificación
        if (newLevel.level > oldLevel) {
            // TODO: Mostrar celebración de subida de nivel
        }
        
        // Actualizar daily goal
        val currentGoal = _dailyGoal.value
        if (currentGoal != null) {
            _dailyGoal.value = DailyGoal.fromXP(
                currentGoal.currentXP + xpAmount,
                currentGoal.targetXP
            )
        }
        
        // Check achievements que dependen de XP
        updateAchievements()
    }
    
    /**
     * Actualizar racha
     */
    suspend fun updateStreak(token: String) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        
        // TODO: Descomentar cuando el backend esté listo
        // val progressResponse = apiService.getProgress("Bearer $token")
        // if (progressResponse.isSuccessful && progressResponse.body() != null) {
        //     cachedProgress = progressResponse.body()!!
        //     val lastActivityDate = cachedProgress.maxByOrNull { it.lastActivity }?.lastActivity
        //     
        //     if (lastActivityDate != null) {
        //         val newStreak = calculateStreakFromDate(lastActivityDate)
        //         _currentStreak.value = newStreak
        //     }
        // }
        
        // MOCK DATA temporal - mantener racha actual
        _currentStreak.value = _currentStreak.value
    }
    
    /**
     * Calcular racha desde la última fecha de actividad
     */
    private fun calculateStreakFromDate(lastActivityISO: String): Int {
        try {
            val lastActivity = LocalDate.parse(lastActivityISO.split("T")[0])
            val today = LocalDate.now()
            val daysDiff = ChronoUnit.DAYS.between(lastActivity, today)
            
            return when {
                daysDiff == 0L -> _currentStreak.value // Mismo día, mantener racha
                daysDiff == 1L -> _currentStreak.value + 1 // Día consecutivo, +1
                else -> 1 // Reset racha, empezar de nuevo
            }
        } catch (e: Exception) {
            return 1 // Default: racha de 1 día
        }
    }
    
    /**
     * Actualizar achievements basado en progreso actual
     */
    private fun updateAchievements() {
        val unlockedAchievements = mutableListOf<Achievement>()
        
        Achievements.ALL.forEach { achievement ->
            val isUnlocked = when (achievement.id) {
                // LECCIONES
                "primera_leccion" -> totalLessonsCompleted >= 1
                "leccion_10" -> totalLessonsCompleted >= 10
                "leccion_50" -> totalLessonsCompleted >= 50
                "leccion_100" -> totalLessonsCompleted >= 100
                "todos_modulos" -> cachedProgress.count { it.percent >= 100 } >= 8
                
                // RACHA
                "racha_3" -> _currentStreak.value >= 3
                "racha_7" -> _currentStreak.value >= 7
                "racha_30" -> _currentStreak.value >= 30
                "racha_100" -> _currentStreak.value >= 100
                "racha_365" -> _currentStreak.value >= 365
                
                // PRECISIÓN
                "quiz_perfecto" -> perfectQuizzes >= 1
                "quiz_perfecto_10" -> perfectQuizzes >= 10
                "precision_90" -> (cachedStats?.precisionGlobal ?: 0f) >= 90f
                "sin_vidas_perdidas" -> quizzesWithFullHearts >= 5
                "precision_100" -> totalLessonsCompleted >= 5 // MOCK: basado en lecciones completadas
                
                // VELOCIDAD - TODO: Implementar con datos reales
                "speed_round_oro" -> false
                "memory_rapido" -> false
                "leccion_rapida" -> false
                "quiz_10_min" -> false
                "dia_completo" -> false
                
                // MAESTRÍA
                "modulo_completado" -> cachedProgress.any { it.percent >= 100 }
                "senas_50" -> (cachedStats?.senasDominadas ?: 0) >= 50
                "senas_200" -> (cachedStats?.senasDominadas ?: 0) >= 200
                
                // ESPECIAL
                "primer_dia" -> totalLessonsCompleted >= 1
                "nivel_50" -> (_userLevel.value?.level ?: 1) >= 50
                
                else -> false
            }
            
            val progress = when (achievement.id) {
                "primera_leccion" -> totalLessonsCompleted.coerceAtMost(1)
                "leccion_10" -> totalLessonsCompleted.coerceAtMost(10)
                "leccion_50" -> totalLessonsCompleted.coerceAtMost(50)
                "leccion_100" -> totalLessonsCompleted.coerceAtMost(100)
                "racha_3" -> _currentStreak.value.coerceAtMost(3)
                "racha_7" -> _currentStreak.value.coerceAtMost(7)
                "racha_30" -> _currentStreak.value.coerceAtMost(30)
                "racha_100" -> _currentStreak.value.coerceAtMost(100)
                "racha_365" -> _currentStreak.value.coerceAtMost(365)
                else -> if (isUnlocked) achievement.requirement else 0
            }
            
            val updatedAchievement = achievement.copy(
                isUnlocked = isUnlocked,
                progress = progress,
                unlockedAt = if (isUnlocked && achievement.unlockedAt == null) {
                    LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                } else {
                    achievement.unlockedAt
                }
            )
            
            unlockedAchievements.add(updatedAchievement)
            
            // Crear notificación si se acaba de desbloquear
            if (isUnlocked && !achievement.isUnlocked) {
                val notification = AchievementNotification(updatedAchievement)
                _pendingNotifications.value = _pendingNotifications.value + notification
            }
        }
        
        _achievements.value = unlockedAchievements
    }
    
    /**
     * Check achievements de racha específicamente
     */
    private fun checkStreakAchievements(streak: Int) {
        val streakAchievements = listOf("racha_3", "racha_7", "racha_30", "racha_100", "racha_365")
        val currentAchievements = _achievements.value.toMutableList()
        
        streakAchievements.forEach { id ->
            val achievement = Achievements.getById(id)
            if (achievement != null && streak >= achievement.requirement) {
                val index = currentAchievements.indexOfFirst { it.id == id }
                if (index != -1 && !currentAchievements[index].isUnlocked) {
                    // Desbloquear achievement
                    currentAchievements[index] = currentAchievements[index].copy(
                        isUnlocked = true,
                        progress = achievement.requirement,
                        unlockedAt = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                    )
                    
                    // Notificación
                    val notification = AchievementNotification(currentAchievements[index])
                    _pendingNotifications.value = _pendingNotifications.value + notification
                }
            }
        }
        
        _achievements.value = currentAchievements
    }
    
    /**
     * Marcar notificación como mostrada
     */
    fun dismissNotification(notification: AchievementNotification) {
        _pendingNotifications.value = _pendingNotifications.value.filter { it != notification }
    }
    
    /**
     * Obtener leaderboard
     */
    suspend fun getLeaderboard(
        type: String, // "weekly", "all_time", "friends"
        token: String
    ): Result<LeaderboardResponse> {
        return try {
            // TODO: Implementar endpoint de leaderboard en backend
            // Por ahora retorna mock data
            val mockEntries = listOf(
                LeaderboardEntry(
                    userId = "1",
                    username = "Estudiante Pro",
                    totalXP = 2450,
                    weeklyXP = 350,
                    level = 12,
                    rank = 1
                ),
                LeaderboardEntry(
                    userId = "2",
                    username = "LSM Maestro",
                    totalXP = 2100,
                    weeklyXP = 280,
                    level = 11,
                    rank = 2
                ),
                LeaderboardEntry(
                    userId = "3",
                    username = "Usuario Estudiante",
                    totalXP = totalXP,
                    weeklyXP = 150,
                    level = _userLevel.value?.level ?: 1,
                    rank = 15
                )
            )
            
            Result.success(
                LeaderboardResponse(
                    leaderboardType = type,
                    entries = mockEntries,
                    userRank = 15,
                    totalUsers = 247
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Incrementar contadores internos (para achievements)
     */
    fun incrementLessonCount() {
        totalLessonsCompleted++
        updateAchievements()
    }
    
    fun incrementPerfectQuiz() {
        perfectQuizzes++
        updateAchievements()
    }
    
    fun incrementQuizWithFullHearts() {
        quizzesWithFullHearts++
        updateAchievements()
    }
}
