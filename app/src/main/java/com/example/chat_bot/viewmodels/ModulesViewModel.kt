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
 * Módulo de aprendizaje con información de progreso
 */
data class ModuleWithProgress(
    val id: Int,
    val code: String,
    val title: String,
    val description: String,
    val sortOrder: Int,
    val completedCount: Int = 0, // Desde backend cuando esté disponible
    val totalCount: Int = 20, // Mock por ahora
    val isLocked: Boolean = false
)

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
    private val _modules = MutableStateFlow<List<ModuleWithProgress>>(emptyList())
    val modules: StateFlow<List<ModuleWithProgress>> = _modules.asStateFlow()
    
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
                    val backendModules = response.body()!!
                    
                    // Mapear ModuleResponse a ModuleWithProgress
                    _modules.value = backendModules.mapIndexed { index, module ->
                        ModuleWithProgress(
                            id = module.id,
                            code = module.code,
                            title = module.title,
                            description = module.description ?: "Sin descripción",
                            sortOrder = module.sortOrder,
                            completedCount = if (index < 2) 15 else 0, // Mock: primeros 2 con progreso
                            totalCount = getTotalCountForModule(module.code),
                            isLocked = index > 3 // Mock: primeros 4 desbloqueados
                        )
                    }.sortedBy { it.sortOrder }
                } else {
                    _errorMessage.value = "Error al cargar módulos"
                    useMockModules()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                useMockModules()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Retorna el número de lecciones según el código del módulo
     */
    private fun getTotalCountForModule(code: String): Int {
        return when (code) {
            "abecedario" -> 26
            "numeros" -> 20
            "colores" -> 13
            "animales" -> 27
            "comida" -> 30
            "familia" -> 15
            "hogar" -> 25
            "frutas" -> 22
            else -> 20
        }
    }
    
    /**
     * Usa módulos mock si el backend no está disponible
     */
    private fun useMockModules() {
        _modules.value = listOf(
            ModuleWithProgress(1, "abecedario", "Abecedario", "Aprende las 26 letras de LSM", 1, 20, 26, false),
            ModuleWithProgress(2, "numeros", "Números", "Cuenta del 0 al 100 en señas", 2, 9, 20, false),
            ModuleWithProgress(3, "colores", "Colores", "Colores básicos y sus variantes", 3, 0, 13, false),
            ModuleWithProgress(4, "animales", "Animales", "Animales domésticos y salvajes", 4, 0, 27, false),
            ModuleWithProgress(5, "comida", "Comida", "Alimentos y bebidas comunes", 5, 0, 30, true),
            ModuleWithProgress(6, "familia", "Familia", "Miembros de la familia", 6, 0, 15, true),
            ModuleWithProgress(7, "hogar", "Hogar", "Objetos y espacios del hogar", 7, 0, 25, true),
            ModuleWithProgress(8, "frutas", "Frutas", "Frutas tropicales y comunes", 8, 0, 22, true)
        )
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
