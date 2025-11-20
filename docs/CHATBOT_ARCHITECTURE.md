# 🤖 Arquitectura del Chatbot LSM - BorregoBot

**Fecha:** 17 de Noviembre, 2025  
**Proyecto:** EnSeñas LSM  
**Versión:** 2.0 (WhatsApp-style con NLP)

---

## 📋 OVERVIEW

BorregoBot es un asistente virtual inteligente diseñado para:
- Enseñar LSM de forma conversacional
- Responder dudas sobre señas
- Guiar a usuarios nuevos (onboarding)
- Practicar vocabulario de forma interactiva
- Dar feedback contextual

**Interfaz:** WhatsApp-style chat (burbujas, timestamps, avatares)  
**NLP Engine:** Gemini API (Google) o GPT-4 (OpenAI)  
**Video Playback:** ExoPlayer integrado en burbujas de chat

---

## 🎯 FEATURES PRINCIPALES

### 1. Conversación Natural
- **Input:** TextField en bottom con emoji picker
- **Output:** Burbujas de mensaje (usuario: derecha, bot: izquierda)
- **Typing indicator:** "BorregoBot está escribiendo..."
- **Timestamps:** Formato "HH:mm" subtle

### 2. Video LSM en Chat
- Videos embebidos en burbujas de mensaje
- Play/pause inline
- Thumbnail con play button
- Mini player (300x200dp)

### 3. Quick Replies
- Botones predefinidos debajo del último mensaje
- Max 3 opciones por mensaje
- Desaparecen al seleccionar
- Ejemplos:
  - "Sí" / "No" / "Más información"
  - "¿Cómo se dice...?" / "Practicar" / "Quiz"

### 4. Tutorial Flow (Onboarding)
```
Bot: ¡Hola! Soy BorregoBot 🐏
     Estoy aquí para ayudarte a aprender LSM.
     ¿Es tu primera vez en EnSeñas?
     
Quick Replies: [Sí, es mi primera vez] [Ya conozco la app]

--- Si "Primera vez" ---

Bot: ¡Genial! Te voy a enseñar lo básico.
     Empecemos con el abecedario LSM.
     [VIDEO: Abecedario A-Z animado]
     
Bot: ¿Quieres practicar ahora?
Quick Replies: [Sí, practicar] [Después]
```

### 5. Contextual Help
- Detectar preguntas: "¿Cómo se dice hola?"
- Responder con video + texto
- Ofrecer práctica relacionada

---

## 🏗️ ARQUITECTURA TÉCNICA

### Capa de Presentación (UI)
```
screens/
  ChatBotScreen.kt (600+ líneas)
    - Main chat interface
    - LazyColumn para mensajes
    - TextField bottom
    - QuickReplyButtons
    - TypingIndicator
    
  components/
    MessageBubble.kt
      - UserMessage (derecha, azul)
      - BotMessage (izquierda, gris)
      - VideoMessage (con ExoPlayer)
      - QuickReplyMessage
      
    ChatInput.kt
      - TextField con send button
      - Emoji picker
      - Voice input (futuro)
```

### Capa de Datos (Data)
```
data/models/
  ChatMessage.kt
    - id: String
    - content: String
    - timestamp: Long
    - isFromUser: Boolean
    - messageType: MessageType enum
    - videoUrl: String?
    - quickReplies: List<String>?
    
  MessageType enum
    - TEXT
    - VIDEO
    - QUICK_REPLY
    - TYPING_INDICATOR
    
data/repository/
  ChatRepository.kt
    - sendMessage(message: String)
    - getResponse(userMessage: String): Flow<ChatMessage>
    - loadHistory(): List<ChatMessage>
    - saveHistory(messages: List<ChatMessage>)
    
data/api/
  NLPService.kt
    - analyzeIntent(message: String): Intent
    - generateResponse(intent: Intent): String
    - getRelatedVideo(intent: Intent): String?
```

### Capa de Lógica (ViewModel)
```
viewmodels/
  ChatViewModel.kt
    - messages: StateFlow<List<ChatMessage>>
    - isTyping: StateFlow<Boolean>
    - sendMessage(text: String)
    - handleQuickReply(reply: String)
    - loadConversationHistory()
```

---

## 🔌 INTEGRACIÓN NLP

### Opción A: Gemini API (Recomendado)
**Ventajas:**
- Gratis hasta 60 req/min
- Multimodal (texto + futuras imágenes)
- Latencia baja (~500ms)
- SDK Android oficial

**Implementación:**
```kotlin
// build.gradle.kts
implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

// NLPService.kt
class GeminiNLPService(private val apiKey: String) {
    private val model = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )
    
    suspend fun generateResponse(userMessage: String): String {
        val prompt = """
        Eres BorregoBot, un asistente de aprendizaje de LSM.
        Usuario: $userMessage
        Responde de forma amigable y concisa.
        Si preguntan por una seña, menciona que puedes mostrar un video.
        """.trimIndent()
        
        val response = model.generateContent(prompt)
        return response.text ?: "Lo siento, no entendí."
    }
    
    fun analyzeIntent(message: String): Intent {
        return when {
            message.contains("cómo se dice", ignoreCase = true) -> Intent.ASK_SIGN
            message.contains("practicar", ignoreCase = true) -> Intent.PRACTICE
            message.contains("quiz", ignoreCase = true) -> Intent.QUIZ
            message.contains("ayuda", ignoreCase = true) -> Intent.HELP
            else -> Intent.GENERAL_QUESTION
        }
    }
}

enum class Intent {
    ASK_SIGN,
    PRACTICE,
    QUIZ,
    HELP,
    GENERAL_QUESTION
}
```

### Opción B: GPT-4 (Alternativa)
**Ventajas:**
- Más poderoso
- Mejor comprensión contextual

**Desventajas:**
- Requiere API key de pago
- Latencia mayor (~1s)

**Implementación:**
```kotlin
// Retrofit API
interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}

data class ChatRequest(
    val model: String = "gpt-4",
    val messages: List<Message>,
    val temperature: Float = 0.7
)
```

---

## 🎨 DISEÑO UI/UX

### Message Bubbles
```kotlin
// UserMessage (derecha)
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
    contentAlignment = Alignment.CenterEnd
) {
    Surface(
        color = AzulTec,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 4.dp
        )
    ) {
        Text(
            text = message.content,
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}

// BotMessage (izquierda)
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
) {
    // Avatar
    Image(
        painter = painterResource(id = R.drawable.borrego_normal),
        contentDescription = "BorregoBot",
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    )
    
    Spacer(modifier = Modifier.width(8.dp))
    
    // Bubble
    Surface(
        color = GrisClaro,
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = message.content,
                color = GrisOscuro
            )
            
            if (message.videoUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                VideoPlayerInline(videoUrl = message.videoUrl)
            }
        }
    }
}
```

### Video Player Inline
```kotlin
@Composable
fun VideoPlayerInline(videoUrl: String) {
    var isPlaying by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .width(250.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
    ) {
        // ExoPlayer view
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(videoUrl))
                        prepare()
                    }
                }
            },
            modifier = Modifier.matchParentSize()
        )
        
        // Play button overlay
        if (!isPlaying) {
            IconButton(
                onClick = { isPlaying = true },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
```

### Quick Replies
```kotlin
@Composable
fun QuickReplyButtons(
    replies: List<String>,
    onReplyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        replies.forEach { reply ->
            OutlinedButton(
                onClick = { onReplyClick(reply) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AzulTec
                )
            ) {
                Text(reply, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
```

---

## 📊 FLUJO DE DATOS

```
User types message
    ↓
ChatViewModel.sendMessage(text)
    ↓
Add user message to messages list
    ↓
Set isTyping = true
    ↓
ChatRepository.getResponse(text)
    ↓
NLPService.analyzeIntent(text)
    ↓
if Intent.ASK_SIGN:
    - Extract word from message
    - GET /dictionary/{word}
    - Return text + video URL
else if Intent.PRACTICE:
    - Return "¿Qué módulo quieres practicar?"
    - Quick replies: ["Abecedario", "Números", "Colores"]
else:
    - GeminiAPI.generateResponse(text)
    ↓
Add bot message to messages list
    ↓
Set isTyping = false
    ↓
UI updates automatically (StateFlow)
```

---

## 🗂️ ESTRUCTURA DE ARCHIVOS

```
app/src/main/java/com/example/chat_bot/
│
├── screens/
│   └── ChatBotScreen.kt (600 líneas)
│
├── ui/components/
│   ├── MessageBubble.kt (200 líneas)
│   ├── ChatInput.kt (150 líneas)
│   ├── QuickReplyButtons.kt (80 líneas)
│   ├── TypingIndicator.kt (60 líneas)
│   └── VideoPlayerInline.kt (120 líneas)
│
├── data/models/
│   ├── ChatMessage.kt (40 líneas)
│   └── Intent.kt (20 líneas)
│
├── data/repository/
│   └── ChatRepository.kt (180 líneas)
│
├── data/api/
│   ├── NLPService.kt (interface)
│   ├── GeminiNLPService.kt (150 líneas)
│   └── OpenAINLPService.kt (opcional)
│
└── viewmodels/
    └── ChatViewModel.kt (200 líneas)
```

**Total estimado:** ~1,800 líneas de código

---

## 🚀 IMPLEMENTACIÓN FASEADA

### Fase 1: UI Base (2-3 horas)
- [x] ChatBotScreen layout
- [x] MessageBubble component
- [x] ChatInput component
- [x] Mock messages para testing

### Fase 2: State Management (1-2 horas)
- [ ] ChatViewModel con StateFlow
- [ ] Repository pattern
- [ ] Message history (in-memory)

### Fase 3: NLP Integration (2-3 horas)
- [ ] Gemini API setup
- [ ] Intent detection
- [ ] Response generation
- [ ] Error handling

### Fase 4: Video Playback (2-3 horas)
- [ ] ExoPlayer integration
- [ ] Inline video player
- [ ] Thumbnail loading
- [ ] Play/pause controls

### Fase 5: Quick Replies (1 hora)
- [ ] QuickReplyButtons component
- [ ] Dynamic reply generation
- [ ] Reply handling

### Fase 6: Tutorial Flow (1-2 horas)
- [ ] Onboarding sequence
- [ ] Guided tour
- [ ] First-time user detection

---

## 🎯 CASOS DE USO

### 1. Usuario pregunta por una seña
```
Usuario: "¿Cómo se dice gracias?"

Bot: "¡Buena pregunta! La seña para 'gracias' es esta:"
     [VIDEO: gracias.mp4]
     "¿Quieres practicar ahora?"
     
Quick Replies: [Sí, practicar] [Ver más señas] [Quiz]
```

### 2. Usuario quiere practicar
```
Usuario: "Quiero practicar"

Bot: "¡Perfecto! ¿Qué módulo te gustaría practicar?"

Quick Replies: [Abecedario] [Números] [Colores] [Animales]

--- Usuario selecciona "Abecedario" ---

Bot: "Vamos a practicar el abecedario LSM.
     Te mostraré una letra y tú me dices cuál es."
     [VIDEO: Letra "A" en LSM]
     
Quick Replies: [A] [E] [O] [U]
```

### 3. Usuario pide ayuda
```
Usuario: "Ayuda"

Bot: "¡Claro! ¿En qué puedo ayudarte?"

Quick Replies:
  [¿Cómo usar la app?]
  [¿Qué es LSM?]
  [Ver tutoriales]
  [Contactar soporte]
```

---

## 🔐 SEGURIDAD Y PRIVACIDAD

### API Keys
- **Gemini API:** Almacenar en `local.properties`
- **No hardcodear** keys en código
- Usar BuildConfig para acceso

```kotlin
// build.gradle.kts
android {
    buildFeatures {
        buildConfig = true
    }
    
    defaultConfig {
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\""
        )
    }
}

// local.properties
GEMINI_API_KEY=tu_api_key_aqui
```

### Data Privacy
- **NO enviar datos personales** a NLP API
- Solo enviar texto del mensaje
- Historial de chat: local only (no backend)
- Opción de borrar historial

---

## 📈 MÉTRICAS Y ANALYTICS

Trackear:
- Mensajes enviados por sesión
- Intents más comunes
- Tasa de uso de Quick Replies
- Videos reproducidos
- Tiempo promedio de sesión

---

## 🎨 PERSONALIZACIÓN

### Temas del Bot
```kotlin
data class BotTheme(
    val bubbleColor: Color,
    val textColor: Color,
    val avatar: Int // Resource ID
)

// Temas disponibles
val ClassicTheme = BotTheme(
    bubbleColor = GrisClaro,
    textColor = GrisOscuro,
    avatar = R.drawable.borrego_normal
)

val FriendlyTheme = BotTheme(
    bubbleColor = AzulTec.copy(alpha = 0.1f),
    textColor = AzulTec,
    avatar = R.drawable.borrego_celebration
)
```

---

## 🐛 TESTING

### Unit Tests
- ChatViewModel logic
- Intent detection accuracy
- Message formatting

### UI Tests
- Message bubble rendering
- Quick reply interaction
- Video playback

### Integration Tests
- NLP API responses
- Backend /dictionary integration
- Error handling (network failures)

---

## 🚀 NEXT STEPS (Post-MVP)

1. **Voice Input:** Reconocimiento de voz para mensajes
2. **Speech Output:** Text-to-speech para respuestas del bot
3. **Multimodal:** Enviar imágenes para identificar señas
4. **Persistent Chat:** Backend storage de conversaciones
5. **Multilingual:** Soporte inglés + español
6. **Gamification:** XP por interactuar con chatbot
7. **Social:** Compartir conversaciones interesantes

---

**Fin de Documento de Arquitectura**  
*EnSeñas LSM - BorregoBot v2.0*
