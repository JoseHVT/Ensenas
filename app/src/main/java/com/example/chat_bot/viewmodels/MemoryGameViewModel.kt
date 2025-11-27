package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.api.RetrofitInstance
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.SignPairResponse
import com.example.chat_bot.data.repository.MemoryGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para Memory Game Screen
 * Gestiona la carga de mazos y el envío de resultados al backend
 */
class MemoryGameViewModel(
    private val tokenManager: TokenManager,
    private val authViewModel: AuthViewModel
) : ViewModel() {
    
    private val repository = MemoryGameRepository(RetrofitInstance.api)
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Mazo de cartas actual
    private val _deck = MutableStateFlow<List<SignPairResponse>>(emptyList())
    val deck: StateFlow<List<SignPairResponse>> = _deck.asStateFlow()
    
    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Último resultado guardado
    private val _lastRunSaved = MutableStateFlow(false)
    val lastRunSaved: StateFlow<Boolean> = _lastRunSaved.asStateFlow()
    
    /**
     * Carga un nuevo mazo de cartas desde el backend
     * @param size Número de pares a cargar (default 8 = 16 cartas)
     */
    fun loadDeck(size: Int = 8) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _lastRunSaved.value = false
            
            try {
                val result = repository.getMemoryDeck(size)
                
                if (result.isSuccess) {
                    _deck.value = result.getOrNull() ?: emptyList()
                    
                    if (_deck.value.isEmpty()) {
                        _errorMessage.value = "No se encontraron pares para el juego"
                    }
                } else {
                    _errorMessage.value = "Error al cargar mazo: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Guarda los resultados de una partida en el backend
     * @param matches Pares encontrados
     * @param attempts Intentos totales
     * @param durationMs Duración en milisegundos
     * @param moduleId ID del módulo (opcional)
     */
    fun saveGameResults(
        matches: Int,
        attempts: Int,
        durationMs: Int,
        moduleId: Int? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val token = tokenManager.getAuthToken().first()
                
                if (token == null) {
                    _errorMessage.value = "No autenticado"
                    _isLoading.value = false
                    return@launch
                }
                
                val result = repository.submitMemoryRun(
                    token = token,
                    matches = matches,
                    attempts = attempts,
                    durationMs = durationMs,
                    streak = null,
                    moduleId = moduleId
                )
                
                if (result.isSuccess) {
                    _lastRunSaved.value = true
                } else {
                    _errorMessage.value = "Error al guardar resultado: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reinicia el estado del juego para una nueva partida
     */
    fun resetGame() {
        _deck.value = emptyList()
        _lastRunSaved.value = false
        _errorMessage.value = null
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
