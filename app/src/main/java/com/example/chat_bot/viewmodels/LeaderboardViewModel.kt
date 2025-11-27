package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.LeaderboardResponse
import com.example.chat_bot.data.repository.GamificationRepository
import com.example.chat_bot.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para LeaderboardScreen - Maneja rankings y leaderboards
 */
class LeaderboardViewModel(
    private val tokenManager: TokenManager,
    private val authViewModel: AuthViewModel
) : ViewModel() {
    
    // Repository de gamificación
    private val gamificationRepository = GamificationRepository(RetrofitInstance.api)
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Leaderboard data
    private val _leaderboardData = MutableStateFlow<LeaderboardResponse?>(null)
    val leaderboardData: StateFlow<LeaderboardResponse?> = _leaderboardData.asStateFlow()
    
    // Tipo actual de leaderboard
    private val _currentType = MutableStateFlow("weekly")
    val currentType: StateFlow<String> = _currentType.asStateFlow()
    
    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadLeaderboard("weekly")
    }
    
    /**
     * Carga el leaderboard del tipo especificado
     */
    fun loadLeaderboard(type: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _currentType.value = type
            
            try {
                val token = tokenManager.getAuthToken().first()
                if (token != null) {
                    val result = gamificationRepository.getLeaderboard(type, token)
                    
                    if (result.isSuccess) {
                        _leaderboardData.value = result.getOrNull()
                    } else {
                        _errorMessage.value = "Error al cargar ranking"
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
     * Recarga el leaderboard actual
     */
    fun refresh() {
        loadLeaderboard(_currentType.value)
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
