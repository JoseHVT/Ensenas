package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.api.RetrofitInstance
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.ModuleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para ModulesScreen - Maneja la lista de módulos
 */
class ModulesViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Lista de módulos
    private val _modules = MutableStateFlow<List<ModuleResponse>>(emptyList())
    val modules: StateFlow<List<ModuleResponse>> = _modules.asStateFlow()
    
    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadModules()
    }
    
    /**
     * Carga los módulos desde el backend
     */
    fun loadModules() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = RetrofitInstance.api.getModules()
                
                if (response.isSuccessful && response.body() != null) {
                    _modules.value = response.body()!!
                } else {
                    _errorMessage.value = "Error al cargar módulos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Actualiza el progreso de un módulo en el backend
     */
    fun updateModuleProgress(moduleId: Int, progress: Int) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getAuthToken().first()
                if (token != null) {
                    // TODO: Llamar endpoint de progreso
                    // RetrofitInstance.api.updateProgress(...)
                }
            } catch (e: Exception) {
                // Silent fail - no afecta la UI
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
