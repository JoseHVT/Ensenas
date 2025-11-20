# 🧪 Plan de Testing - EnSeñas App

## ✅ Navegación y Testing (Opción C)

### 📋 Checklist de Testing

---

## 1. NAVEGACIÓN BÁSICA ✅

### Bottom Navigation Bar
- [ ] Tap en "Inicio" → Navega a HomeScreen
- [ ] Tap en "Módulos" → Navega a ModulesScreen  
- [ ] Tap en "Diccionario" → Navega a DictionaryScreen
- [ ] Tap en "Perfil" → Navega a ProfileScreen
- [ ] Verificar que el icono seleccionado se marca correctamente
- [ ] Verificar transiciones suaves entre pantallas

### HomeScreen Navigation
- [ ] Tap en "Diccionario" card → Navega a DictionaryScreen
- [ ] Tap en "Logros" card → Navega a AchievementsScreen
- [ ] Tap en "Clasificación" card → Navega a LeaderboardScreen
- [ ] Tap en FAB "BorregoBot" → Navega a ChatBotScreen
- [ ] Tap en "Módulos" → Navega a ModulesScreen
- [ ] Verificar que nivel y racha se muestran correctamente

### ProfileScreen Navigation
- [ ] Tap en tarjeta de Logros → Navega a AchievementsScreen
- [ ] Tap en botón Logout → Muestra diálogo de confirmación
- [ ] Confirmar logout → Navega a LoginScreen y limpia stack

---

## 2. CHATBOT (Opción D - NUEVO) ✅

### UI Básica
- [ ] ChatBotScreen se abre correctamente
- [ ] TopAppBar muestra avatar 🐏 y nombre "BorregoBot"
- [ ] Estado "En línea" se muestra en verde
- [ ] Mensajes de bienvenida aparecen automáticamente (3 mensajes)
- [ ] TextField de input está habilitado
- [ ] Botón de enviar está deshabilitado cuando input está vacío

### Mensajes de Bienvenida
- [ ] Mensaje 1: "¡Hola! Soy BorregoBot 🐏" (SYSTEM)
- [ ] Mensaje 2: "Estoy aquí para ayudarte..." (BOT)
- [ ] Mensaje 3: "¿Es tu primera vez en EnSeñas?" con quick replies
- [ ] Quick replies: "Sí, es mi primera vez", "Ya conozco la app", "Solo quiero practicar"
- [ ] Delay de 800ms entre mensajes (efecto typing)

### Enviar Mensajes
- [ ] Escribir "Hola" y enviar
  - [ ] Mensaje aparece en burbuja azul a la derecha
  - [ ] Typing indicator aparece (3 puntos animados)
  - [ ] Respuesta del bot aparece en burbuja gris a la izquierda
  - [ ] Auto-scroll al último mensaje
- [ ] Escribir "¿Cómo se dice gracias?" y enviar
  - [ ] Bot detecta intent ASK_SIGN
  - [ ] Respuesta incluye video placeholder
  - [ ] Quick replies: "Practicar esta seña", "Ver más señas", "Quiz"

### Detección de Intents
- [ ] "Hola" → GREETING (respuesta con bienvenida)
- [ ] "¿Cómo se dice X?" → ASK_SIGN (video + quick replies)
- [ ] "Quiero practicar" → PRACTICE (selección de módulo)
- [ ] "Dame un quiz" → QUIZ (selección de módulo)
- [ ] "Ayuda" → HELP (lista de funciones)
- [ ] "Gracias" → THANKS (respuesta amigable)
- [ ] "Módulo de colores" → MODULE_INFO (info del módulo)
- [ ] "Mi progreso" → STATS (estadísticas)
- [ ] Mensaje aleatorio → GENERAL_QUESTION (opciones generales)

### Quick Replies
- [ ] Quick replies aparecen debajo del mensaje del bot
- [ ] Tap en quick reply envía ese texto como mensaje
- [ ] Quick replies desaparecen después de enviar uno
- [ ] Máximo 3 quick replies visibles
- [ ] Botones con borde azul y texto azul

### Typing Indicator
- [ ] Aparece inmediatamente después de enviar mensaje
- [ ] 3 puntos con animación de alpha (0.3f → 1f)
- [ ] Desaparece cuando llega respuesta del bot
- [ ] Estado "escribiendo..." en TopAppBar

### Video Messages
- [ ] Placeholder de video se muestra (250x140dp)
- [ ] Icono de play ▶️ centrado
- [ ] Nombre del archivo debajo del icono
- [ ] Fondo negro con esquinas redondeadas

### Menú de Opciones
- [ ] Tap en icono de 3 puntos abre menú
- [ ] Opción "Limpiar conversación" limpia chat y recarga bienvenida
- [ ] Opción "Ver perfil del bot" (TODO: implementar navegación)
- [ ] Opción "Ayuda" envía mensaje "Ayuda"

### Navegación
- [ ] Tap en flecha atrás → Vuelve a pantalla anterior
- [ ] Historial de mensajes se mantiene al navegar back/forward (por ahora no persiste)
- [ ] Bottom bar NO se muestra en ChatBotScreen

---

## 3. GAMIFICACIÓN (Opción A) ✅

### AchievementsScreen
- [ ] Navegar desde HomeScreen → Tap en "Logros"
- [ ] TopAppBar muestra "{X}/{25} desbloqueados"
- [ ] Progress ring muestra porcentaje global
- [ ] Filtros de categoría funcionan (Todas, LECCIONES, RACHA, etc.)
- [ ] Logros desbloqueados muestran emoji e icono
- [ ] Logros bloqueados muestran candado 🔒
- [ ] Progress bar visible en logros no desbloqueados
- [ ] Tap en logro muestra animación de scale
- [ ] Scroll suave con animaciones

### LeaderboardScreen
- [ ] Navegar desde HomeScreen → Tap en "Clasificación"
- [ ] 3 tabs: "Semanal", "Todo", "Amigos"
- [ ] Podium muestra Top 3 con orden correcto (2°-1°-3°)
- [ ] Medallas rotando en 1er lugar
- [ ] Alturas diferentes: 1° (180dp), 2° (140dp), 3° (120dp)
- [ ] Bordes de colores: Oro, Plata, Bronce
- [ ] Lista scrollable para rank 4+
- [ ] Usuario actual resaltado con fondo azul claro
- [ ] Tarjeta "Tu Posición" sticky al bottom
- [ ] Cambiar entre tabs actualiza datos

---

## 4. QUIZ Y MEMORY GAME (Opción A/B) ✅

### ModulesScreen
- [ ] Scroll vertical funciona
- [ ] Auto-scroll al módulo actual
- [ ] Módulos bloqueados muestran candado
- [ ] Tap en módulo bloqueado → Shake animation
- [ ] Tap en módulo desbloqueado → Navega a QuizScreen
- [ ] Progress circular muestra progreso correcto
- [ ] Íconos de módulos visibles

### QuizScreen
- [ ] Se abre con moduleId correcto
- [ ] TopAppBar muestra nombre del módulo
- [ ] 3 corazones (vidas) visibles
- [ ] Timer de 30s cuenta regresivamente
- [ ] Barra de progreso actualiza correctamente
- [ ] 4 tipos de quiz:
  - [ ] Video → Texto: Muestra video, seleccionar respuesta
  - [ ] Texto → Imagen: Muestra texto, seleccionar imagen
  - [ ] Video → Múltiple: Video con 4 opciones
  - [ ] Speed Round: Responder en <25s
- [ ] Respuesta correcta → Feedback verde + XP
- [ ] Respuesta incorrecta → Feedback rojo, pierde vida
- [ ] Timer expira → Pierde vida
- [ ] Sin vidas → Game Over screen
- [ ] Completar quiz → Results screen con estrellas
- [ ] Botón volver funciona

### MemoryGameScreen
- [ ] Selector de dificultad: Fácil/Medio/Difícil
- [ ] Fácil: 6 pares (3x4 grid)
- [ ] Medio: 9 pares (3x6 grid)
- [ ] Difícil: 12 pares (4x6 grid)
- [ ] Tap en carta → Flip 3D animation
- [ ] Match correcto → Cartas quedan volteadas
- [ ] Match incorrecto → Cartas se voltean de nuevo
- [ ] Timer cuenta segundos
- [ ] Completar juego → Victory screen
- [ ] Estrellas según tiempo: ⭐⭐⭐ (<2min), ⭐⭐ (<3min), ⭐ (>3min)

---

## 5. ANIMACIONES (Opción B) ✅

### Performance (60 FPS)
- [ ] Abrir Android Profiler
- [ ] Navegar entre pantallas
- [ ] Frame time < 16.67ms (60 FPS)
- [ ] No jank visible durante scroll
- [ ] Animaciones suaves en:
  - [ ] pressAnimation (scale 0.98f)
  - [ ] bounceIn (entrada de cards)
  - [ ] pulse (fire icon en racha)
  - [ ] shake (módulos bloqueados)
  - [ ] infiniteRotation (medalla 1er lugar)

### ShimmerEffect
- [ ] AchievementsScreen loading → Shimmer cards visibles
- [ ] LeaderboardScreen loading → Shimmer list
- [ ] Gradiente se mueve horizontalmente
- [ ] Transición suave a contenido real

### Animated Components
- [ ] AnimatedProgressBar en AchievementsScreen
- [ ] AnimatedCircularProgress en ModulesScreen
- [ ] AnimatedCounter en LeaderboardScreen (XP)
- [ ] TypingText en mensajes del bot
- [ ] FadeCrossfade en transiciones

---

## 6. INTEGRACIÓN BACKEND

### Login/Register
- [ ] Credenciales válidas → Token guardado
- [ ] Token inválido → Mensaje de error
- [ ] Network error → Mensaje amigable

### HomeScreen Data
- [ ] Racha actual se carga correctamente
- [ ] Nivel de usuario se calcula bien
- [ ] Meta diaria muestra progreso real
- [ ] Username se muestra correctamente

### ModulesScreen Data
- [ ] Lista de módulos carga desde backend
- [ ] Progreso de cada módulo es correcto
- [ ] Módulos bloqueados según prerequisites

### Gamification Data
- [ ] Achievements se cargan desde backend
- [ ] Leaderboard muestra usuarios reales
- [ ] XP total coincide con backend
- [ ] Racha se calcula con fechas reales

---

## 7. EDGE CASES & ERROR HANDLING

### Sin Conexión
- [ ] Login sin internet → Error "Sin conexión"
- [ ] Cargar módulos offline → Mensaje apropiado
- [ ] Chatbot sin internet → "No puedo conectarme ahora"

### Errores de Backend
- [ ] API timeout → Mensaje de error
- [ ] 401 Unauthorized → Redirige a Login
- [ ] 500 Server Error → "Algo salió mal, intenta de nuevo"

### Estados Vacíos
- [ ] Sin achievements desbloqueados → Mensaje motivacional
- [ ] Leaderboard vacío → "Sé el primero en la lista"
- [ ] Sin mensajes en chat → Bienvenida automática

---

## 🎯 CRITERIOS DE ÉXITO

### Funcionalidad
- ✅ Todas las navegaciones funcionan
- ✅ No crashes ni ANR (Application Not Responding)
- ✅ Datos se cargan correctamente
- ✅ Animaciones fluidas a 60 FPS

### UX/UI
- ✅ Transiciones suaves
- ✅ Feedback visual en interacciones
- ✅ Loading states apropiados
- ✅ Error messages claros

### Performance
- ✅ Inicio de app < 3 segundos
- ✅ Navegación < 300ms
- ✅ No memory leaks
- ✅ Battery drain normal

---

## 📱 TESTING EN DISPOSITIVOS

### Emulador
- [ ] Pixel 6 API 34 (Android 14)
- [ ] Resolución 1080x2400
- [ ] Modo oscuro ON/OFF

### Dispositivo Real (opcional)
- [ ] Android 11+ (API 30+)
- [ ] Diferentes tamaños de pantalla
- [ ] Conexión WiFi/Datos móviles

---

## 🐛 BUGS CONOCIDOS (Para Iterar)

### ChatBot
- [ ] TODO: Integrar ExoPlayer para videos reales
- [ ] TODO: Guardar historial en Room Database
- [ ] TODO: Integrar Gemini API real
- [ ] TODO: Navegación desde quick replies a módulos/quizzes

### Gamification
- [ ] TODO: Conectar con backend real en achievements
- [ ] TODO: Leaderboard con datos reales (mock data actualmente)
- [ ] TODO: Notificaciones push para achievements

### General
- [ ] TODO: Dark mode completo
- [ ] TODO: Soporte para tablets
- [ ] TODO: Accesibilidad (TalkBack)

---

## 🚀 COMANDOS ÚTILES

### Build & Run
```powershell
# Compilar proyecto
.\gradlew build

# Instalar en emulador
.\gradlew installDebug

# Run tests
.\gradlew test
```

### Debugging
```powershell
# Logcat filtrado
adb logcat | Select-String "EnSeñas"

# Limpiar y rebuild
.\gradlew clean build
```

---

## ✅ CHECKLIST FINAL

Antes de considerar completo el testing:

- [ ] Todas las navegaciones funcionan sin crashes
- [ ] Chatbot responde a todos los intents
- [ ] Gamificación muestra datos correctos
- [ ] Animaciones a 60 FPS
- [ ] No errores de compilación
- [ ] No warnings críticos
- [ ] README actualizado con nuevas features
- [ ] Commits organizados en Git

---

**Estado Actual: LISTO PARA TESTING** ✅

**Fecha:** 17 de Noviembre, 2025  
**Versión:** 1.0.0-beta  
**Branch:** Chat_Bot_Dev
