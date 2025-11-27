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
    
    private val _leaderboard = MutableStateFlow<LeaderboardResponse?>(null)
    val leaderboard: StateFlow<LeaderboardResponse?> = _leaderboard.asStateFlow()
    
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
            // Obtener estadísticas del usuario desde backend
            val statsResponse = apiService.getUserStats("Bearer $token")
            val progressResponse = apiService.getUserProgress("Bearer $token")
            val levelResponse = apiService.getUserLevelInfo("Bearer $token")
            val streakResponse = apiService.getStreakInfo("Bearer $token")
            
            // Procesar estadísticas si están disponibles
            if (statsResponse.isSuccessful && statsResponse.body() != null) {
                cachedStats = statsResponse.body()
                // StatsResponse tiene: precisionGlobal, tiempoTotalMs, rachaActual, senasDominadas
                // Los contadores de lecciones/quizzes se deducen de otras fuentes
                totalLessonsCompleted = 0 // TODO: Obtener de DailyActivity
                perfectQuizzes = 0 // TODO: Obtener de quiz attempts
                quizzesWithFullHearts = 0 // TODO: Obtener de quiz attempts
            } else {
                // Fallback a valores por defecto
                totalLessonsCompleted = 0
                perfectQuizzes = 0
                quizzesWithFullHearts = 0
            }
            
            // Procesar progreso de módulos
            if (progressResponse.isSuccessful && progressResponse.body() != null) {
                cachedProgress = progressResponse.body() ?: emptyList()
            }
            
            // Obtener nivel y XP del usuario
            if (levelResponse.isSuccessful && levelResponse.body() != null) {
                val levelInfo = levelResponse.body()!!
                totalXP = levelInfo.totalXp
                _userLevel.value = UserLevel(
                    level = levelInfo.currentLevel,
                    currentXP = levelInfo.currentLevelXp,
                    requiredXP = levelInfo.requiredXp,
                    progress = levelInfo.progress
                )
            } else {
                // Fallback: calcular nivel desde XP mock
                totalXP = 0
                _userLevel.value = UserLevel.calculateLevel(totalXP)
            }
            
            // Obtener información de racha
            if (streakResponse.isSuccessful && streakResponse.body() != null) {
                val streakInfo = streakResponse.body()!!
                _currentStreak.value = streakInfo.currentStreak
            } else {
                _currentStreak.value = 0
            }
            
            // Actualizar achievements basado en datos reales
            updateAchievements()
            
            // Calcular daily goal desde transacciones de XP
            val xpTransactionsResponse = apiService.getXPTransactions("Bearer $token")
            var todayXP = 0
            if (xpTransactionsResponse.isSuccessful && xpTransactionsResponse.body() != null) {
                val today = java.time.LocalDate.now().toString()
                todayXP = xpTransactionsResponse.body()!!
                    .filter { it.createdAt.startsWith(today) }
                    .sumOf { it.amount }
            }
            _dailyGoal.value = DailyGoal.fromXP(todayXP, 50)
            
            Result.success(Unit)
        } catch (e: Exception) {
            // En caso de error, usar datos por defecto para no bloquear la app
            totalXP = 0
            totalLessonsCompleted = 0
            _userLevel.value = UserLevel.calculateLevel(0)
            _currentStreak.value = 0
            _dailyGoal.value = DailyGoal.fromXP(0, 50)
            
            Result.failure(e)
        }
    }
    
    /**
     * Agregar XP al usuario
     */
    suspend fun addXP(xpAmount: Int, source: String, sourceId: Int?, token: String): Result<Unit> {
        return try {
            // Llamar al backend para award XP
            val request = XPAwardRequest(
                amount = xpAmount,
                source = source,
                sourceId = sourceId,
                description = "XP ganado desde $source"
            )
            
            val response = apiService.awardXP("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                val awardResponse = response.body()!!
                
                // Actualizar XP total local
                totalXP = awardResponse.totalXp
                
                // Actualizar nivel
                val oldLevel = _userLevel.value?.level ?: 1
                val newLevel = UserLevel.calculateLevel(totalXP)
                _userLevel.value = newLevel
                
                // Si subió de nivel, crear notificación
                if (newLevel.level > oldLevel) {
                    // Disparar celebración de nivel (manejado por UI)
                    android.util.Log.d("GamificationRepo", "¡Nivel subido! ${oldLevel} → ${newLevel.level}")
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
                
                Result.success(Unit)
            } else {
                // Fallback local si el backend falla
                totalXP += xpAmount
                val newLevel = UserLevel.calculateLevel(totalXP)
                _userLevel.value = newLevel
                
                Result.failure(Exception("Error al guardar XP en backend: ${response.code()}"))
            }
        } catch (e: Exception) {
            // En caso de error de red, actualizar localmente
            totalXP += xpAmount
            _userLevel.value = UserLevel.calculateLevel(totalXP)
            
            Result.failure(e)
        }
    }
    
    /**
     * Actualizar racha
     */
    suspend fun updateStreak(token: String): Result<Int> {
        return try {
            // Llamar al backend para actualizar la racha del día actual
            val updateRequest = StreakUpdateRequest(
                activityType = "quiz", // Por defecto, quiz
                xpEarned = 0
            )
            
            val updateResponse = apiService.updateStreak("Bearer $token", updateRequest)
            
            if (updateResponse.isSuccessful && updateResponse.body() != null) {
                // Obtener info actualizada de racha
                val streakInfoResponse = apiService.getStreakInfo("Bearer $token")
                
                if (streakInfoResponse.isSuccessful && streakInfoResponse.body() != null) {
                    val streakInfo = streakInfoResponse.body()!!
                    val newStreak = streakInfo.currentStreak
                    val oldStreak = _currentStreak.value
                    
                    _currentStreak.value = newStreak
                    
                    // Si la racha aumentó, check achievements
                    if (newStreak > oldStreak) {
                        checkStreakAchievements(newStreak)
                    }
                    
                    return Result.success(newStreak)
                }
            }
            
            // Si falla, mantener racha actual
            Result.success(_currentStreak.value)
        } catch (e: Exception) {
            // En caso de error, no modificar la racha
            Result.failure(e)
        }
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
     * TODO: Implementar endpoint de leaderboard en backend (PRIORIDAD 1 - Task 3)
     * Endpoint esperado: GET /leaderboard/{type} (weekly, monthly, all-time)
     */
    suspend fun getLeaderboard(
        type: String, // "weekly", "monthly", "all_time"
        token: String
    ): Result<LeaderboardResponse> {
        return try {
            // TODO: Descomentar cuando el backend esté listo
            // val response = apiService.getLeaderboard("Bearer $token", type)
            // if (response.isSuccessful && response.body() != null) {
            //     val leaderboardData = response.body()!!
            //     _leaderboard.value = leaderboardData
            //     return Result.success(leaderboardData)
            // }
            
            // MOCK DATA temporal - Simulación de leaderboard
            val mockEntries = listOf(
                LeaderboardEntry(
                    userId = "mock_user_1",
                    username = "Estudiante Pro",
                    totalXP = 2450,
                    weeklyXP = 350,
                    level = 12,
                    rank = 1
                ),
                LeaderboardEntry(
                    userId = "mock_user_2",
                    username = "LSM Maestro",
                    totalXP = 2100,
                    weeklyXP = 280,
                    level = 11,
                    rank = 2
                ),
                LeaderboardEntry(
                    userId = "current_user", // Placeholder para usuario actual
                    username = "Usuario Estudiante",
                    totalXP = totalXP,
                    weeklyXP = 150,
                    level = _userLevel.value?.level ?: 1,
                    rank = 15
                )
            )
            
            val mockResponse = LeaderboardResponse(
                leaderboardType = type,
                entries = mockEntries,
                userRank = 15,
                totalUsers = 247
            )
            
            _leaderboard.value = mockResponse
            Result.success(mockResponse)
        } catch (e: Exception) {
            android.util.Log.e("GamificationRepo", "Error fetching leaderboard: ${e.message}")
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
