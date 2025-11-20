package com.example.chat_bot.data.repository

import com.example.chat_bot.data.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Chat Repository - Maneja la lógica de mensajes y NLP
 */
class ChatRepository {
    
    private val messageHistory = mutableListOf<ChatMessage>()
    private var conversationContext = ConversationContext()
    
    /**
     * Enviar mensaje del usuario y obtener respuesta del bot
     */
    fun sendMessageAndGetResponse(userMessage: String): Flow<ChatMessage> = flow {
        // 1. Agregar mensaje del usuario
        val userMsg = ChatMessage(
            content = userMessage,
            isFromUser = true
        )
        messageHistory.add(userMsg)
        
        // 2. Detectar intent
        val intent = detectIntent(userMessage)
        conversationContext = conversationContext.copy(currentIntent = intent)
        
        // 3. Mostrar typing indicator
        delay(500) // Simular que el bot está "pensando"
        
        // 4. Generar respuesta según intent
        val botResponse = generateResponse(intent, userMessage)
        
        // 5. Agregar respuesta del bot al historial
        messageHistory.add(botResponse)
        
        // 6. Emitir respuesta
        emit(botResponse)
    }
    
    /**
     * Detectar la intención del mensaje del usuario
     */
    private fun detectIntent(message: String): ChatIntent {
        val lowerMessage = message.lowercase()
        
        return when {
            // Saludos
            lowerMessage.contains("hola") || 
            lowerMessage.contains("buenos días") || 
            lowerMessage.contains("buenas") -> ChatIntent.GREETING
            
            // Preguntar por una seña
            lowerMessage.contains("cómo se dice") || 
            lowerMessage.contains("como se dice") ||
            lowerMessage.contains("seña de") ||
            lowerMessage.contains("señar") -> ChatIntent.ASK_SIGN
            
            // Practicar
            lowerMessage.contains("practicar") || 
            lowerMessage.contains("práctica") ||
            lowerMessage.contains("ejercicio") -> ChatIntent.PRACTICE
            
            // Quiz
            lowerMessage.contains("quiz") || 
            lowerMessage.contains("examen") ||
            lowerMessage.contains("prueba") -> ChatIntent.QUIZ
            
            // Ayuda
            lowerMessage.contains("ayuda") || 
            lowerMessage.contains("no entiendo") ||
            lowerMessage.contains("explica") -> ChatIntent.HELP
            
            // Gracias
            lowerMessage.contains("gracias") || 
            lowerMessage.contains("graciaz") -> ChatIntent.THANKS
            
            // Info de módulo
            lowerMessage.contains("módulo") || 
            lowerMessage.contains("modulo") ||
            lowerMessage.contains("lección") -> ChatIntent.MODULE_INFO
            
            // Stats
            lowerMessage.contains("progreso") || 
            lowerMessage.contains("estadísticas") ||
            lowerMessage.contains("nivel") -> ChatIntent.STATS
            
            else -> ChatIntent.GENERAL_QUESTION
        }
    }
    
    /**
     * Generar respuesta del bot según el intent
     */
    private fun generateResponse(intent: ChatIntent, userMessage: String): ChatMessage {
        val template = when (intent) {
            ChatIntent.GREETING -> BotResponseTemplate(
                text = "¡Hola! 👋 Soy BorregoBot, tu asistente para aprender LSM.\n¿En qué puedo ayudarte hoy?",
                quickReplies = listOf(
                    "¿Cómo se dice...?",
                    "Quiero practicar",
                    "Dame un quiz"
                )
            )
            
            ChatIntent.ASK_SIGN -> {
                val word = extractWordFromQuestion(userMessage)
                BotResponseTemplate(
                    text = if (word != null) {
                        "¡Buena pregunta! La seña para '$word' es esta:"
                    } else {
                        "¿Qué palabra quieres aprender a señar?"
                    },
                    videoUrl = if (word != null) {
                        "https://example.com/videos/$word.mp4" // TODO: Real video URL
                    } else null,
                    quickReplies = if (word != null) {
                        listOf("Practicar esta seña", "Ver más señas", "Quiz")
                    } else {
                        listOf("Hola", "Gracias", "Por favor")
                    }
                )
            }
            
            ChatIntent.PRACTICE -> BotResponseTemplate(
                text = "¡Perfecto! ¿Qué módulo te gustaría practicar?",
                quickReplies = listOf(
                    "Abecedario",
                    "Números",
                    "Colores",
                    "Animales"
                ),
                followUpAction = FollowUpAction.NAVIGATE_TO_MODULE
            )
            
            ChatIntent.QUIZ -> BotResponseTemplate(
                text = "¡Genial! Vamos a poner a prueba tus conocimientos.\n¿De qué módulo quieres el quiz?",
                quickReplies = listOf(
                    "Abecedario",
                    "Números",
                    "Colores"
                ),
                followUpAction = FollowUpAction.START_QUIZ
            )
            
            ChatIntent.HELP -> BotResponseTemplate(
                text = "¡Claro! Estoy aquí para ayudarte.\n\nPuedo:\n• Enseñarte señas 🤟\n• Practicar contigo 📝\n• Darte quizzes 🎯\n• Mostrar tu progreso 📊\n\n¿Qué necesitas?",
                quickReplies = listOf(
                    "¿Cómo usar la app?",
                    "¿Qué es LSM?",
                    "Ver tutoriales"
                )
            )
            
            ChatIntent.THANKS -> BotResponseTemplate(
                text = "¡De nada! 😊 Estoy aquí para ayudarte.\n¿Hay algo más en lo que pueda asistirte?",
                quickReplies = listOf(
                    "Sí, otra pregunta",
                    "No, gracias"
                )
            )
            
            ChatIntent.MODULE_INFO -> {
                val moduleName = extractModuleName(userMessage)
                BotResponseTemplate(
                    text = if (moduleName != null) {
                        "El módulo de $moduleName contiene lecciones para aprender las señas básicas de este tema.\n\n¿Quieres empezar?"
                    } else {
                        "Tenemos varios módulos:\n• Abecedario\n• Números\n• Colores\n• Animales\n• Comida\n• Familia\n\n¿Cuál te interesa?"
                    },
                    quickReplies = listOf("Empezar módulo", "Ver todos")
                )
            }
            
            ChatIntent.STATS -> BotResponseTemplate(
                text = "Déjame consultar tus estadísticas...\n\n📊 Tu progreso:\n• Nivel: 5\n• XP Total: 245\n• Racha: 7 días 🔥\n• Módulos completados: 3/8\n\n¡Vas muy bien! 🎉",
                quickReplies = listOf(
                    "Ver detalles",
                    "Seguir practicando"
                ),
                followUpAction = FollowUpAction.SHOW_STATS
            )
            
            ChatIntent.GENERAL_QUESTION -> BotResponseTemplate(
                text = "Interesante pregunta. Aunque no tengo una respuesta específica, puedo ayudarte con:\n\n• Aprender nuevas señas\n• Practicar las que ya conoces\n• Hacer quizzes\n\n¿Qué prefieres?",
                quickReplies = listOf(
                    "Aprender señas",
                    "Practicar",
                    "Quiz"
                )
            )
        }
        
        return ChatMessage(
            content = template.text,
            isFromUser = false,
            messageType = if (template.videoUrl != null) MessageType.VIDEO else MessageType.TEXT,
            videoUrl = template.videoUrl,
            quickReplies = template.quickReplies
        )
    }
    
    /**
     * Extraer palabra de pregunta "¿Cómo se dice X?"
     */
    private fun extractWordFromQuestion(message: String): String? {
        val patterns = listOf(
            "cómo se dice (.+)".toRegex(),
            "como se dice (.+)".toRegex(),
            "seña de (.+)".toRegex(),
            "seña para (.+)".toRegex()
        )
        
        for (pattern in patterns) {
            val match = pattern.find(message.lowercase())
            if (match != null) {
                return match.groupValues[1].trim()
                    .removeSuffix("?")
                    .removeSuffix(".")
                    .trim()
            }
        }
        
        return null
    }
    
    /**
     * Extraer nombre de módulo del mensaje
     */
    private fun extractModuleName(message: String): String? {
        val modules = listOf("abecedario", "números", "colores", "animales", "comida", "familia")
        
        for (module in modules) {
            if (message.lowercase().contains(module)) {
                return module.capitalize()
            }
        }
        
        return null
    }
    
    /**
     * Obtener historial de mensajes
     */
    fun getMessageHistory(): List<ChatMessage> = messageHistory.toList()
    
    /**
     * Limpiar historial
     */
    fun clearHistory() {
        messageHistory.clear()
        conversationContext = ConversationContext()
    }
    
    /**
     * Obtener mensajes de bienvenida
     */
    fun getWelcomeMessages(): List<ChatMessage> {
        return listOf(
            ChatMessage(
                content = "¡Hola! Soy BorregoBot 🐏",
                isFromUser = false,
                messageType = MessageType.SYSTEM
            ),
            ChatMessage(
                content = "Estoy aquí para ayudarte a aprender Lengua de Señas Mexicana de forma divertida e interactiva.",
                isFromUser = false
            ),
            ChatMessage(
                content = "¿Es tu primera vez en EnSeñas?",
                isFromUser = false,
                quickReplies = listOf(
                    "Sí, es mi primera vez",
                    "Ya conozco la app",
                    "Solo quiero practicar"
                )
            )
        )
    }
}
