package com.example.chat_bot.data.models

/**
 * Chat Message Model
 */
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromUser: Boolean,
    val messageType: MessageType = MessageType.TEXT,
    val videoUrl: String? = null,
    val quickReplies: List<String>? = null,
    val isRead: Boolean = false
)

enum class MessageType {
    TEXT,           // Mensaje de texto simple
    VIDEO,          // Mensaje con video LSM embebido
    QUICK_REPLY,    // Mensaje con botones de respuesta rápida
    TYPING_INDICATOR, // Indicador de "escribiendo..."
    SYSTEM          // Mensajes del sistema (ej: "Iniciaste una conversación")
}

/**
 * Intent Detection
 */
enum class ChatIntent {
    ASK_SIGN,           // "¿Cómo se dice X?"
    PRACTICE,           // "Quiero practicar"
    QUIZ,               // "Dame un quiz"
    HELP,               // "Ayuda" / "No entiendo"
    GREETING,           // "Hola" / "Buenos días"
    THANKS,             // "Gracias"
    MODULE_INFO,        // "¿Qué es el módulo X?"
    STATS,              // "¿Cuál es mi progreso?"
    GENERAL_QUESTION    // Pregunta general
}

/**
 * Conversation Context
 */
data class ConversationContext(
    val currentIntent: ChatIntent? = null,
    val waitingForInput: Boolean = false,
    val expectedInputType: String? = null, // "module_name", "word", etc.
    val lastModuleDiscussed: String? = null,
    val conversationDepth: Int = 0
)

/**
 * Bot Response Template
 */
data class BotResponseTemplate(
    val text: String,
    val videoUrl: String? = null,
    val quickReplies: List<String>? = null,
    val followUpAction: FollowUpAction? = null
)

enum class FollowUpAction {
    NAVIGATE_TO_MODULE,
    START_QUIZ,
    SHOW_DICTIONARY,
    SHOW_STATS,
    NONE
}
