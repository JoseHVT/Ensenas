package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.api.RetrofitInstance
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.StatsResponse
import com.example.chat_bot.viewmodels.AuthViewModel
import com.example.chat_bot.data.models.UserLevel
import com.example.chat_bot.data.models.DailyGoal
import com.example.chat_bot.ui.components.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para HomeScreen - Maneja estadísticas del usuario
 */
class HomeViewModel(
    private val tokenManager: TokenManager,
    private val authViewModel: AuthViewModel
) : ViewModel() {
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Datos del usuario
    private val _userLevel = MutableStateFlow<UserLevel?>(null)
    val userLevel: StateFlow<UserLevel?> = _userLevel.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _userName = MutableStateFlow("Usuario")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    private val _dailyGoal = MutableStateFlow<DailyGoal?>(null)
    val dailyGoal: StateFlow<DailyGoal?> = _dailyGoal.asStateFlow()
    
    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadUserData()
    }
    
    /**
     * Carga los datos del usuario desde el backend
     */
    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Obtener token
                val token = tokenManager.getAuthToken().first()
                if (token == null) {
                    _errorMessage.value = "No hay sesión activa"
                    return@launch
                }
                
                // Llamar al backend
                val response = RetrofitInstance.api.getUserStats("Bearer $token")
                
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    updateStatsFromBackend(stats)
                } else {
                    // Si falla, usar datos mock
                    useMockData()
                    _errorMessage.value = "Usando datos de ejemplo"
                }
            } catch (e: Exception) {
                // Si hay error, usar datos mock
                useMockData()
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Actualiza el estado con datos del backend
     */
    private fun updateStatsFromBackend(stats: StatsResponse) {
        // Usar señas dominadas * 10 como aproximación de XP
        val estimatedXP = stats.senasDominadas * 10
        val level = UserLevel.calculateLevel(estimatedXP)
        _userLevel.value = level
        
        // Actualizar racha desde backend
        _currentStreak.value = stats.rachaActual
        
        // Crear daily goal basado en XP estimado (mock por ahora)
        _dailyGoal.value = DailyGoal.fromXP(currentXP = 25, target = 50)
        
        // Obtener nombre de usuario
        viewModelScope.launch {
            val name = tokenManager.getUserName().first()
            _userName.value = name ?: "Usuario Estudiante"
        }
    }
    
    /**
     * Usar datos mock si el backend no está disponible
     */
    private fun useMockData() {
        _userLevel.value = UserLevel.calculateLevel(1250) // Nivel 3-4
        _currentStreak.value = 7
        _dailyGoal.value = DailyGoal.fromXP(currentXP = 25, target = 50)
        viewModelScope.launch {
            val name = tokenManager.getUserName().first()
            _userName.value = name ?: "Usuario Estudiante"
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Recarga las estadísticas del usuario (para pull-to-refresh)
     */
    fun refreshStats() {
        loadUserData()
    }
}
