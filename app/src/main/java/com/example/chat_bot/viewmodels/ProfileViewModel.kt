package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.auth.AuthRepository
import com.example.chat_bot.data.auth.AuthState
import com.example.chat_bot.data.auth.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para ProfileScreen - Maneja perfil de usuario y logout
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
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
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadUserInfo()
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
     * Cierra la sesión del usuario
     */
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                authRepository.signOut()
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
