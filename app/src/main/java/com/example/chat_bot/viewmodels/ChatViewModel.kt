package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.models.ChatMessage
import com.example.chat_bot.data.models.MessageType
import com.example.chat_bot.data.repository.ChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel del Chatbot - Gestiona el estado de la conversación
 */
class ChatViewModel : ViewModel() {
    
    private val repository = ChatRepository()
    
    // Estado de mensajes
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    // Estado de typing indicator
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()
    
    // Estado del input
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()
    
    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        // Cargar mensajes de bienvenida al iniciar
        loadWelcomeMessages()
    }
    
    /**
     * Cargar mensajes de bienvenida
     */
    private fun loadWelcomeMessages() {
        viewModelScope.launch {
            try {
                val welcomeMessages = repository.getWelcomeMessages()
                
                // Mostrar mensajes uno por uno con delay para efecto de typing
                welcomeMessages.forEach { message ->
                    delay(800) // Delay entre mensajes
                    _messages.value = _messages.value + message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar mensajes de bienvenida"
            }
        }
    }
    
    /**
     * Actualizar texto del input
     */
    fun updateInputText(text: String) {
        _inputText.value = text
    }
    
    /**
     * Enviar mensaje del usuario
     */
    fun sendMessage(text: String = _inputText.value) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            try {
                // 1. Agregar mensaje del usuario inmediatamente
                val userMessage = ChatMessage(
                    content = text.trim(),
                    isFromUser = true
                )
                _messages.value = _messages.value + userMessage
                
                // 2. Limpiar input
                _inputText.value = ""
                
                // 3. Mostrar typing indicator
                _isTyping.value = true
                
                // 4. Agregar typing indicator visual
                val typingIndicator = ChatMessage(
                    content = "",
                    isFromUser = false,
                    messageType = MessageType.TYPING_INDICATOR
                )
                _messages.value = _messages.value + typingIndicator
                
                // 5. Obtener respuesta del repositorio
                repository.sendMessageAndGetResponse(text.trim())
                    .collect { botResponse ->
                        // Remover typing indicator
                        _messages.value = _messages.value.filterNot { 
                            it.messageType == MessageType.TYPING_INDICATOR 
                        }
                        
                        // Agregar respuesta del bot
                        _messages.value = _messages.value + botResponse
                        
                        // Ocultar typing indicator
                        _isTyping.value = false
                    }
                
            } catch (e: Exception) {
                // Remover typing indicator en caso de error
                _messages.value = _messages.value.filterNot { 
                    it.messageType == MessageType.TYPING_INDICATOR 
                }
                _isTyping.value = false
                
                // Mostrar mensaje de error
                val errorMessage = ChatMessage(
                    content = "Lo siento, ocurrió un error. Por favor intenta de nuevo.",
                    isFromUser = false
                )
                _messages.value = _messages.value + errorMessage
                _error.value = e.message
            }
        }
    }
    
    /**
     * Manejar selección de quick reply
     */
    fun handleQuickReply(reply: String) {
        // Enviar el texto del quick reply como mensaje
        sendMessage(reply)
    }
    
    /**
     * Limpiar conversación
     */
    fun clearConversation() {
        viewModelScope.launch {
            repository.clearHistory()
            _messages.value = emptyList()
            _inputText.value = ""
            _error.value = null
            
            // Recargar mensajes de bienvenida
            loadWelcomeMessages()
        }
    }
    
    /**
     * Limpiar error
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Scroll automático al último mensaje
     */
    fun shouldScrollToBottom(): Boolean {
        return _messages.value.isNotEmpty()
    }
}
