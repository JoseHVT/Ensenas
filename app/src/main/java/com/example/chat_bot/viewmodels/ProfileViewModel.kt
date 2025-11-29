package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.api.RetrofitInstance
import com.example.chat_bot.data.auth.AuthRepository
import com.example.chat_bot.data.auth.AuthState
import com.example.chat_bot.data.auth.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para ProfileScreen - Maneja perfil de usuario y logout
 */
class ProfileViewModel(
    private val authViewModel: AuthViewModel,
    private val tokenManager: TokenManager
) : ViewModel() {

    
    // Estado de autenticación
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // Nombre de usuario
    private val _userName = MutableStateFlow("Usuario")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    // Email de usuario
    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()
    
    // Estadísticas del usuario
    private val _modulesCompleted = MutableStateFlow(0)
    val modulesCompleted: StateFlow<Int> = _modulesCompleted.asStateFlow()
    
    private val _totalProgress = MutableStateFlow(0)
    val totalProgress: StateFlow<Int> = _totalProgress.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _signsLearned = MutableStateFlow(0)
    val signsLearned: StateFlow<Int> = _signsLearned.asStateFlow()
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadUserInfo()
        loadUserStats()
    }
    
    /**
     * Carga la información del usuario desde TokenManager
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            tokenManager.getUserName().collect { name ->
                _userName.value = name ?: "Usuario"
            }
        }
        
        viewModelScope.launch {
            tokenManager.getUserEmail().collect { email ->
                _userEmail.value = email ?: ""
            }
        }
    }
    
    /**
     * Carga las estadísticas del usuario desde el backend
     */
    private fun loadUserStats() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val token = tokenManager.getAuthToken().first()
                if (token != null) {
                    val response = RetrofitInstance.api.getUserStats("Bearer $token")
                    
                    if (response.isSuccessful && response.body() != null) {
                        val stats = response.body()!!
                        // Calcular módulos completados basándose en señas dominadas (aprox. 30 señas por módulo)
                        _modulesCompleted.value = stats.senasDominadas / 30
                        // Calcular progreso total basándose en señas dominadas (meta: 500 señas)
                        _totalProgress.value = ((stats.senasDominadas.toFloat() / 500f) * 100f).toInt().coerceAtMost(100)
                        _currentStreak.value = stats.rachaActual
                        _signsLearned.value = stats.senasDominadas
                    } else {
                        // Usar datos mock si falla
                        useMockStats()
                    }
                } else {
                    useMockStats()
                }
            } catch (e: Exception) {
                useMockStats()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Usa datos mock para las estadísticas
     */
    private fun useMockStats() {
        _modulesCompleted.value = 3
        _totalProgress.value = 45
        _currentStreak.value = 7
        _signsLearned.value = 127
    }

    /**
     * Cierra la sesión del usuario
     *
     * "No value passed for parameter authViewModel"
     * (para asegurar que use AuthViewModel)
     */
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                authViewModel.signOut()
                tokenManager.clearAll()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al cerrar sesión")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
