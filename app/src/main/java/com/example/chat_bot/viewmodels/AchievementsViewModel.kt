package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.Achievement
import com.example.chat_bot.data.repository.GamificationRepository
import com.example.chat_bot.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para AchievementsScreen - Maneja logros del usuario
 */
class AchievementsViewModel(
    private val tokenManager: TokenManager,
    private val authViewModel: AuthViewModel
) : ViewModel() {
    
    // Repository de gamificación
    private val gamificationRepository = GamificationRepository(RetrofitInstance.api)
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Achievements del usuario
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadAchievements()
    }
    
    /**
     * Carga los achievements del usuario
     */
    fun loadAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val token = tokenManager.getAuthToken().first()
                if (token != null) {
                    // Inicializar gamification data que incluye achievements
                    val result = gamificationRepository.initializeGamificationData(token)
                    
                    if (result.isSuccess) {
                        // Observar achievements desde el repository
                        gamificationRepository.achievements.collect { achievementsList ->
                            _achievements.value = achievementsList
                        }
                    } else {
                        _errorMessage.value = "Error al cargar logros"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
