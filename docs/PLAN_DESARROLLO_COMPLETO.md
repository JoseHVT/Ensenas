# 🎯 PLAN MAESTRO DE DESARROLLO - EnSeñas LSM App

## 📊 ESTADO ACTUAL DEL PROYECTO

### ✅ Completado (40%)
- **8 Pantallas básicas funcionando:**
  - SplashScreen, LoginScreen, RegisterScreen
  - HomeScreen, ModulesScreen, DictionaryScreen, ProfileScreen
  - DictionaryDetailScreen con ExoPlayer
- **Backend FastAPI:** 7 routers operacionales
- **Base de datos:** SQLite con 43 señas y 8 módulos
- **Navegación:** BottomBar con 4 tabs
- **Tema:** Azul Tec implementado

### 🚧 Pendiente (60%)
- Diseño profesional nivel Duolingo
- Funcionalidades completas (quizzes, memory game, progreso)
- Chatbot de LSM integrado
- Sistema de gamificación avanzado
- Animaciones fluidas
- Backend conectado 100%

---

## 🎨 FASE 1: REDISEÑO PROFESIONAL DE UI/UX (Prioridad MÁXIMA)

### Objetivo: Transformar la app a nivel Duolingo/Babbel

#### 1.1 Sistema de Diseño Profesional

**Colores refinados:**
```kotlin
// Paleta principal
AzulTecPrimary = Color(0xFF0039A6)       // Azul Tec
AzulTecLight = Color(0xFF4A90E2)         // Azul claro para highlights
AzulTecDark = Color(0xFF002366)          // Azul oscuro para depth

// Gamificación (estilo Duolingo)
VerdeExito = Color(0xFF58CC02)           // Verde brillante
VerdeExitoLight = Color(0xFF89E219)      // Verde claro
AmarilloOro = Color(0xFFFFC800)          // Oro para racha/XP
NaranjaEnergia = Color(0xFFFF9600)       // Naranja para notificaciones
RojoError = Color(0xFFFF4B4B)            // Rojo suave

// Neutrales
BlancoNieve = Color(0xFFF7F8FA)          // Fondo claro
GrisClaro = Color(0xFFE5E7EB)            // Bordes sutiles
GrisMedio = Color(0xFF9CA3AF)            // Texto secundario
GrisOscuro = Color(0xFF374151)           // Texto principal

// Gradientes
val AzulGradient = Brush.verticalGradient(
    colors = listOf(AzulTecPrimary, AzulTecLight)
)
val VerdeGradient = Brush.verticalGradient(
    colors = listOf(VerdeExito, VerdeExitoLight)
)
```

**Tipografía mejorada:**
```kotlin
val EnsenasTypography = Typography(
    // Títulos grandes (pantallas principales)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp
    ),
    
    // Títulos de sección
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    
    // Tarjetas y módulos
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    
    // Cuerpo principal
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    
    // Botones
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

#### 1.2 Componentes Reutilizables (Design System)

**EnsenasButton.kt** - Botones profesionales con variantes:
```kotlin
@Composable
fun EnsenasButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val colors = when(variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = AzulTecPrimary,
            contentColor = Color.White
        )
        ButtonVariant.Success -> ButtonDefaults.buttonColors(
            containerColor = VerdeExito,
            contentColor = Color.White
        )
        ButtonVariant.Outline -> ButtonDefaults.outlinedButtonColors(
            contentColor = AzulTecPrimary
        )
    }
    
    // Implementación con animaciones, loading state, etc.
}
```

**EnsenasCard.kt** - Tarjetas con hover effects:
```kotlin
@Composable
fun EnsenasCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isPressed = true },
                    onRelease = { isPressed = false },
                    onTap = { onClick?.invoke() }
                )
            }
            .scale(if (isPressed) 0.98f else 1f),
        // Animación de escala al presionar
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = elevation + 2.dp
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        content()
    }
}
```

**ProgressBar.kt** - Barra de progreso animada:
```kotlin
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = VerdeExito,
    backgroundColor: Color = GrisClaro,
    height: Dp = 12.dp,
    animationDuration: Int = 1000
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        )
    )
    
    // Implementación con gradientes y brillo
}
```

---

## 🏗️ FASE 2: FUNCIONALIDADES COMPLETAS POR PANTALLA

### 2.1 HomeScreen MEJORADO (Dashboard Profesional)

**Diseño estilo Duolingo:**
```
┌─────────────────────────────────────┐
│  👋 ¡Hola, [Nombre]!                │
│  [Borrego animado saludando]         │
├─────────────────────────────────────┤
│  🔥 RACHA                            │
│  ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐     │
│  │ ✓ │ │ ✓ │ │ ✓ │ │ 7 │ │   │     │
│  │ L │ │ M │ │ M │ │ J │ │ V │     │
│  └───┘ └───┘ └───┘ └───┘ └───┘     │
│  7 días 🔥  ¡No pierdas tu racha!   │
├─────────────────────────────────────┤
│  ⭐ EXPERIENCIA                      │
│  245 XP / 500 XP para nivel 3       │
│  [═══════════════░░░░░] 49%         │
├─────────────────────────────────────┤
│  📚 LECCIÓN DIARIA                  │
│  ┌─────────────────────────────┐   │
│  │ 🎯 Colores Básicos          │   │
│  │ Nivel: Principiante         │   │
│  │ [▶ CONTINUAR] (5 min)       │   │
│  └─────────────────────────────┘   │
├─────────────────────────────────────┤
│  📊 TU PROGRESO                     │
│  ┌─────┐ ┌─────┐ ┌─────┐           │
│  │ 75% │ │ 12  │ │ 89% │           │
│  │Mód. │ │Dias │ │Acierto         │
│  └─────┘ └─────┘ └─────┘           │
├─────────────────────────────────────┤
│  🎮 RETOS SEMANALES                 │
│  □ Completa 5 lecciones (3/5)       │
│  □ Practica 10 señas nuevas (10/10)✅│
│  □ Gana 100 XP (68/100)             │
└─────────────────────────────────────┘
```

**Funcionalidades:**
- Animación de racha con fuego parpadeante
- XP bar con efecto de llenado
- Lección sugerida basada en progreso
- Retos semanales con recompensas
- Notificaciones de recordatorio
- Gráficos de progreso semanal

### 2.2 ModulesScreen PROFESIONAL (Mapa de Aprendizaje)

**Diseño tipo "Camino de aprendizaje":**
```
      ┌─────┐
      │  1  │ Abecedario ✅ (100%)
      └──┬──┘
         │
      ┌──┴──┐
      │  2  │ Números ✅ (100%)
      └──┬──┘
         │
      ┌──┴──┐
      │  3  │ Colores 🔓 (45%) ← TÚ ESTÁS AQUÍ
      └──┬──┘
         │
      ┌──┴──┐
      │  4  │ Animales 🔒
      └──┬──┘
         │
      ┌──┴──┐
      │  5  │ Familia 🔒
      └─────┘
```

**Elementos interactivos:**
- Path animado que se ilumina al desbloquear
- Estrellas por nivel (1-3 estrellas por módulo)
- Trofeos por completar 100%
- Módulos con animación de "unlock"
- Cofres de recompensa al completar
- Mini-quiz de repaso rápido

### 2.3 QuizScreen (NUEVO - Gamificación)

**4 tipos de quiz:**

**A) Multiple Choice con video:**
```
┌─────────────────────────────────────┐
│  Pregunta 2 de 10              ❤️❤️❤️│
│  [════════════░░░░░░░░] 20%         │
├─────────────────────────────────────┤
│  ¿Qué seña es esta?                 │
│                                     │
│  [🎬 Video reproduciéndose]         │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ A) Perro                    │   │
│  ├─────────────────────────────┤   │
│  │ B) Gato ✅                  │   │
│  ├─────────────────────────────┤   │
│  │ C) León                     │   │
│  ├─────────────────────────────┤   │
│  │ D) Tigre                    │   │
│  └─────────────────────────────┘   │
│                                     │
│  [VERIFICAR RESPUESTA]              │
└─────────────────────────────────────┘
```

**B) Completar la seña (Gesture recognition):**
Usuario ve una palabra y debe hacer la seña frente a la cámara
(Integración con ML Kit para reconocimiento de gestos)

**C) Traducción texto → seña:**
Usuario ve texto y debe seleccionar el video correcto

**D) Speed round (Contra reloj):**
30 segundos para identificar máximo de señas posibles

**Sistema de vidas:**
- 3 corazones iniciales
- Pierdes 1 por respuesta incorrecta
- Recuperas 1 con racha de 3 aciertos

**Recompensas:**
- +10 XP por respuesta correcta
- +50 XP bonus por quiz perfecto (sin errores)
- +100 XP por primera vez completando módulo
- Gemas virtuales para desbloquear personalizaciones

### 2.4 MemoryGameScreen (NUEVO - Juego de Parejas)

**Diseño estilo memoria:**
```
┌─────────────────────────────────────┐
│  🧠 Juego de Memoria LSM            │
│  Tiempo: 1:23  Movimientos: 8      │
├─────────────────────────────────────┤
│  ┌───┐ ┌───┐ ┌───┐ ┌───┐          │
│  │ ? │ │🎬 │ │ ? │ │ ? │          │
│  └───┘ └───┘ └───┘ └───┘          │
│  ┌───┐ ┌───┐ ┌───┐ ┌───┐          │
│  │ ? │ │ ? │ │🐕 │ │ ? │          │
│  └───┘ └───┘ └───┘ └───┘          │
│  ┌───┐ ┌───┐ ┌───┐ ┌───┐          │
│  │ ? │ │ ? │ │ ? │ │🎬 │          │
│  └───┘ └───┘ └───┘ └───┘          │
│                                     │
│  Parejas: 2/6  ⭐⭐⭐              │
└─────────────────────────────────────┘
```

**Mecánica:**
- Emparejar palabra con video de seña
- 3 niveles de dificultad (4x4, 6x6, 8x8)
- Temporizador para puntuación
- Estrellas según tiempo/movimientos
- Modo desafío diario

### 2.5 DictionaryScreen MEJORADO

**Búsqueda avanzada con filtros:**
```
┌─────────────────────────────────────┐
│  🔍 [Buscar señas...]         🎛️   │
├─────────────────────────────────────┤
│  Filtros: [Categoría▼] [A-Z▼]      │
├─────────────────────────────────────┤
│  📁 Colores (13 señas)              │
│  ┌─────────────────────────────┐   │
│  │ 🎬 Amarillo                 │   │
│  │ "Color del sol..."          │   │
│  │ [▶ Ver seña] [⭐Favorito]   │   │
│  ├─────────────────────────────┤   │
│  │ 🎬 Azul                     │   │
│  │ "Color del cielo..."        │   │
│  └─────────────────────────────┘   │
│                                     │
│  📁 Animales (20 señas)             │
│  ...                                │
└─────────────────────────────────────┘
```

**Funcionalidades nuevas:**
- Filtros por categoría, dificultad, favoritos
- Vista grid/list switchable
- Búsqueda por voz
- Favoritos sincronizados
- Historial de búsquedas recientes
- Compartir señas por WhatsApp/Email
- Modo offline con descarga de videos

### 2.6 ProfileScreen AVANZADO

**Perfil completo del aprendiz:**
```
┌─────────────────────────────────────┐
│  [🖼️ Foto perfil editable]          │
│  Juan Pérez                         │
│  @juanito_lsm                       │
│  Nivel 5 - Intermedio 🏅            │
├─────────────────────────────────────┤
│  📊 ESTADÍSTICAS DETALLADAS         │
│  ┌─────┬─────┬─────┬─────┐         │
│  │1,245│ 18  │ 92% │ 7🔥 │         │
│  │ XP  │Dias │Acierto Racha        │
│  └─────┴─────┴─────┴─────┘         │
├─────────────────────────────────────┤
│  🏆 LOGROS (12/25)                  │
│  ✅ Primera Lección                 │
│  ✅ 7 Días de Racha                 │
│  ✅ 100 Señas Aprendidas            │
│  🔒 30 Días de Racha                │
├─────────────────────────────────────┤
│  📈 PROGRESO POR MÓDULO             │
│  Abecedario    [██████████] 100%   │
│  Números       [████████░░] 80%    │
│  Colores       [█████░░░░░] 45%    │
├─────────────────────────────────────┤
│  ⚙️ CONFIGURACIÓN                   │
│  • Notificaciones diarias          │
│  • Recordatorio de práctica         │
│  • Velocidad de videos              │
│  • Modo oscuro                      │
│  • Privacidad y datos               │
│  • Cerrar sesión                    │
└─────────────────────────────────────┘
```

**Sistema de logros:**
- 25 logros desbloqueables
- Badges personalizados
- Ranking semanal entre usuarios
- Certificados de módulos completados
- Compartir logros en redes sociales

---

## 🤖 FASE 3: CHATBOT DE LSM (Innovación Principal)

### 3.1 Arquitectura del Chatbot

Ya tienes base de chatbot (`ChatBotBottomSheet.kt`, `ChatBotBubble.kt`). Voy a mejorarlo:

**ChatBotScreen.kt** - Asistente virtual LSM:

```kotlin
/**
 * Chatbot inteligente que:
 * 1. Responde preguntas sobre señas en texto
 * 2. Muestra videos de señas cuando se lo pides
 * 3. Corrige tu práctica de señas (con cámara)
 * 4. Sugiere lecciones personalizadas
 * 5. Te ayuda con dudas de gramática LSM
 */
@Composable
fun ChatBotScreen() {
    // Interfaz estilo WhatsApp/Telegram
    // Con burbujas de chat
    // Teclado rápido con sugerencias
    // Integración con Gemini/GPT para NLP
}
```

**Funcionalidades del chatbot:**

**A) Consultas de señas:**
```
Usuario: "¿Cómo se dice perro?"
Bot: 🤖 ¡Claro! Aquí está la seña para "PERRO" 🐕
     [Video del perro reproduciéndose]
     ¿Te gustaría practicar señas de animales?
```

**B) Práctica guiada:**
```
Bot: 🤖 Vamos a practicar los colores. 
     ¿Puedes mostrarme la seña de "ROJO"?
     [Cámara se activa]
Usuario: [Hace la seña]
Bot: 🤖 ✅ ¡Excelente! Tu seña es correcta.
     Ahora intenta "AZUL"...
```

**C) Conversaciones en LSM:**
```
Bot: 🤖 Vamos a tener una conversación básica.
     Salúdame en LSM.
Usuario: [Hace seña "HOLA"]
Bot: 🤖 ¡Hola! ¿Cómo estás?
     [Muestra video de "¿Cómo estás?"]
Usuario: [Responde con seña]
```

**D) Tutoriales interactivos:**
```
Bot: 🤖 Tutorial: Abecedario LSM
     Paso 1: La letra "A"
     [Video + Explicación]
     📝 Consejo: Los dedos deben estar juntos
     [Imagen close-up de la mano]
     
     ¿Listo para practicar? Di "sí" o haz la seña ✅
```

### 3.2 Integración con ML Kit / TensorFlow Lite

**Hand Landmark Detection para reconocimiento:**
```kotlin
// MediaPipe Hands para detectar posición de manos
class SignRecognitionService {
    private val handDetector = HandLandmarkerHelper()
    
    fun recognizeSign(bitmap: Bitmap): SignResult {
        val landmarks = handDetector.detectLandmarks(bitmap)
        return classifySign(landmarks)
    }
    
    private fun classifySign(landmarks: List<Landmark>): SignResult {
        // Modelo TFLite entrenado con dataset de LSM
        // Retorna la seña reconocida + confidence score
    }
}
```

**Entrenamiento del modelo:**
1. Dataset de señas LSM (imágenes/videos)
2. Extracción de landmarks (21 puntos de la mano)
3. Modelo de clasificación (CNN + LSTM para gestos dinámicos)
4. Exportar a TFLite para Android

---

## 📊 FASE 4: GAMIFICACIÓN AVANZADA

### 4.1 Sistema de XP y Niveles

```kotlin
data class UserLevel(
    val currentXP: Int,
    val currentLevel: Int,
    val nextLevelXP: Int,
    val title: String // "Principiante", "Aprendiz", "Intermedio", etc.
)

// Cálculo de XP por nivel (estilo exponencial)
fun calculateXPForLevel(level: Int): Int {
    return (100 * level * 1.5).toInt()
}

// XP por actividades:
const val XP_COMPLETE_LESSON = 50
const val XP_PERFECT_QUIZ = 100
const val XP_DAILY_PRACTICE = 20
const val XP_NEW_SIGN_LEARNED = 10
const val XP_HELP_FRIEND = 30
```

### 4.2 Sistema de Racha (Streak)

```kotlin
data class Streak(
    val currentStreak: Int,      // Días consecutivos
    val longestStreak: Int,      // Récord personal
    val freezes: Int,            // "Congeladores" para proteger racha
    val lastPracticeDate: LocalDate
)

// Recompensas por racha:
// 7 días  → Badge "Semana perfecta"
// 30 días → Badge "Mes de dedicación" + 500 XP
// 100 días → Badge "Centurión LSM" + Borrego dorado
```

### 4.3 Logros y Badges

```kotlin
enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: Int,
    val xpReward: Int
) {
    FIRST_LESSON("first_lesson", "Primera Lección", "Completa tu primera lección", R.drawable.badge_first, 50),
    WEEK_STREAK("week_streak", "Semana Perfecta", "7 días de racha", R.drawable.badge_week, 100),
    HUNDRED_SIGNS("hundred_signs", "Políglota LSM", "Aprende 100 señas", R.drawable.badge_hundred, 500),
    PERFECT_MODULE("perfect_module", "Perfeccionista", "Completa módulo sin errores", R.drawable.badge_perfect, 200),
    EARLY_BIRD("early_bird", "Madrugador", "Practica antes de las 8am", R.drawable.badge_morning, 30),
    NIGHT_OWL("night_owl", "Búho Nocturno", "Practica después de las 10pm", R.drawable.badge_night, 30),
    SPEED_DEMON("speed_demon", "Rayo LSM", "Completa quiz en menos de 1 min", R.drawable.badge_speed, 150),
    HELPING_HAND("helping_hand", "Mano Amiga", "Comparte 10 señas", R.drawable.badge_share, 100)
}
```

---

## 🔧 FASE 5: BACKEND Y BASE DE DATOS

### 5.1 Migración a MySQL/PostgreSQL

**Script de migración:**
```sql
-- Estructura mejorada con tracking de progreso
CREATE TABLE user_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(128) NOT NULL,
    module_id INT NOT NULL,
    lesson_id INT,
    xp_earned INT DEFAULT 0,
    stars_earned INT DEFAULT 0, -- 0-3 estrellas
    completed_at TIMESTAMP,
    time_spent INT, -- segundos
    accuracy_rate DECIMAL(5,2), -- % de aciertos
    FOREIGN KEY (user_uid) REFERENCES users(uid),
    FOREIGN KEY (module_id) REFERENCES modules(id)
);

CREATE TABLE user_achievements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(128) NOT NULL,
    achievement_id VARCHAR(50) NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_uid, achievement_id)
);

CREATE TABLE quiz_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(128) NOT NULL,
    quiz_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    time_taken INT, -- segundos
    hearts_remaining INT,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_streaks (
    user_uid VARCHAR(128) PRIMARY KEY,
    current_streak INT DEFAULT 0,
    longest_streak INT DEFAULT 0,
    freezes_available INT DEFAULT 2,
    last_practice_date DATE,
    FOREIGN KEY (user_uid) REFERENCES users(uid)
);
```

### 5.2 Nuevos Endpoints FastAPI

```python
# app/routers/progress.py
@router.post("/progress/lesson")
async def record_lesson_completion(
    lesson_completion: LessonCompletionCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Registra completación de lección y calcula XP ganado"""
    pass

@router.get("/progress/streak")
async def get_user_streak(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Obtiene racha actual del usuario"""
    pass

# app/routers/achievements.py
@router.get("/achievements")
async def get_user_achievements(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Lista logros desbloqueados y pendientes"""
    pass

# app/routers/leaderboard.py
@router.get("/leaderboard/weekly")
async def get_weekly_leaderboard(
    db: Session = Depends(get_db)
):
    """Top 10 usuarios de la semana por XP"""
    pass
```

---

## 🎬 FASE 6: ANIMACIONES Y TRANSICIONES

### 6.1 Animaciones de éxito (estilo Duolingo)

**Cuando completas una lección:**
```kotlin
@Composable
fun LessonCompleteAnimation() {
    // Confetti explosion
    // Borrego saltando celebrando
    // Sonido de victoria
    // +XP counter animado
    // Estrellas ganadas (1-3)
    // Botón "CONTINUAR" pulsante
}
```

**Cuando subes de nivel:**
```kotlin
@Composable
fun LevelUpAnimation() {
    // Fondo con rayos dorados
    // Badge del nuevo nivel
    // "¡NIVEL 5!" con efecto de zoom
    // Recompensas desbloqueadas
    // Compartir en redes sociales
}
```

### 6.2 Micro-interacciones

- Botones con efecto ripple
- Cards con hover effect (escala 0.98)
- Progress bars con shimmer
- Loading states skeleton
- Pull to refresh en listas
- Swipe gestures para navegación

---

## 📱 FASE 7: FEATURES PREMIUM

### 7.1 Modo Offline

- Descarga de módulos para uso sin internet
- Cache de videos en res/raw
- Sincronización cuando vuelve conexión
- Indicador de contenido descargado

### 7.2 Modo Oscuro

```kotlin
@Composable
fun EnsenasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = AzulTecLight,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
    } else {
        lightColorScheme(
            primary = AzulTecPrimary,
            background = BlancoNieve,
            surface = Color.White
        )
    }
}
```

### 7.3 Personalización

- Avatares del Borrego (diferentes colores/accesorios)
- Temas de colores alternativos
- Velocidad de reproducción de videos
- Tamaño de fuente ajustable

---

## 🧪 FASE 8: TESTING Y CALIDAD

### 8.1 Tests Unitarios

```kotlin
@Test
fun `calculateXPForLevel returns correct values`() {
    assertEquals(100, calculateXPForLevel(1))
    assertEquals(150, calculateXPForLevel(2))
    assertEquals(225, calculateXPForLevel(3))
}

@Test
fun `user streak increments correctly`() {
    val streak = Streak(currentStreak = 5, ...)
    val updated = streak.incrementStreak()
    assertEquals(6, updated.currentStreak)
}
```

### 8.2 Tests de UI

```kotlin
@Test
fun `login screen shows error with invalid email`() {
    composeTestRule.setContent {
        LoginScreen(...)
    }
    
    composeTestRule.onNodeWithText("Email").performTextInput("invalid")
    composeTestRule.onNodeWithText("Iniciar Sesión").performClick()
    composeTestRule.onNodeWithText("Correo inválido").assertIsDisplayed()
}
```

---

## 📅 CRONOGRAMA DE IMPLEMENTACIÓN

### Semana 1-2: UI/UX Profesional
- [ ] Rediseñar todas las pantallas con nuevo design system
- [ ] Crear componentes reutilizables
- [ ] Implementar animaciones básicas
- [ ] Modo oscuro

### Semana 3-4: Funcionalidades Core
- [ ] QuizScreen con 4 tipos de quiz
- [ ] MemoryGameScreen completo
- [ ] Sistema de XP y niveles
- [ ] Sistema de racha

### Semana 5-6: Chatbot
- [ ] Integrar ML Kit para reconocimiento de manos
- [ ] Entrenar modelo TFLite con dataset LSM
- [ ] Implementar conversaciones del bot
- [ ] Tutoriales interactivos

### Semana 7-8: Backend y Sincronización
- [ ] Migrar a MySQL
- [ ] Implementar todos los endpoints
- [ ] Sistema de autenticación Firebase completo
- [ ] Sincronización de progreso

### Semana 9-10: Gamificación Avanzada
- [ ] 25 logros implementados
- [ ] Leaderboard semanal
- [ ] Sistema de recompensas
- [ ] Compartir en redes sociales

### Semana 11-12: Polish y Testing
- [ ] Tests unitarios y de UI
- [ ] Optimización de performance
- [ ] Accesibilidad (TalkBack)
- [ ] Documentación completa

---

## 💎 MI OPINIÓN Y RECOMENDACIONES

### ✅ Fortalezas actuales del proyecto:
1. **Arquitectura sólida** - Tienes separación clara (screens, navigation, theme)
2. **Backend funcional** - FastAPI con 7 routers es excelente base
3. **Contenido rico** - 43 señas con videos es buen punto de partida
4. **Navegación clara** - BottomBar intuitivo

### 🚀 Oportunidades de mejora críticas:
1. **UI/UX es prioridad #1** - Transformarla a nivel profesional te diferenciará
2. **Chatbot es tu ventaja competitiva** - Ninguna app de LSM tiene esto bien hecho
3. **Gamificación mantendrá usuarios** - Sistema de racha/XP es adictivo
4. **ML para reconocimiento** - Práctica con cámara es game-changer

### 🎯 Mi recomendación de prioridades:

**FASE 1 (Inmediata):** Arreglar errores de compilación ✅
**FASE 2 (Próximos 3 días):** Rediseño completo de UI/UX
**FASE 3 (Próxima semana):** QuizScreen + MemoryGame
**FASE 4 (En 2 semanas):** Chatbot básico funcional
**FASE 5 (En 1 mes):** ML + reconocimiento de señas

---

## 🎬 SIGUIENTE ACCIÓN INMEDIATA

Voy a empezar ahora mismo con:

1. **Crear el nuevo Design System completo**
2. **Rediseñar HomeScreen al nivel profesional**
3. **Implementar QuizScreen funcional**
4. **Mejorar el chatbot existente**

¿Quieres que empiece con el rediseño de HomeScreen estilo Duolingo o prefieres que primero implemente el QuizScreen completo?

También puedo mostrarte un mockup visual de cómo quedaría cada pantalla antes de programarlo.

**¿Por dónde empezamos?** 🚀
