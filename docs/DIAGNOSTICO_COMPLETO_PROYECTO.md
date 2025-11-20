# 📊 DIAGNÓSTICO COMPLETO DEL PROYECTO ENSEÑAS
**Fecha de Análisis:** 20 de Noviembre, 2025  
**Versión del Proyecto:** 1.0-beta  
**Branch Actual:** frontend  
**Analista:** GitHub Copilot

---

## 🎯 RESUMEN EJECUTIVO

### Estado General del Proyecto
- **Progreso Global:** ~60% completado
- **Backend (FastAPI + SQLite):** ✅ 85% funcional
- **Frontend (Android/Kotlin):** ✅ 70% completado
- **Base de Datos:** ✅ SQLite operativa (migración a MySQL pendiente)
- **Autenticación:** ⚠️ Parcial (Firebase Auth configurado pero no integrado)
- **Almacenamiento Multimedia:** ❌ **CRÍTICO** - Firebase Storage no disponible

---

## 📋 ANÁLISIS POR COMPONENTE

### 1️⃣ BACKEND (FastAPI + Python)

#### ✅ **Componentes Implementados**

##### Modelos de Datos (models.py) - 100% ✅
```
✅ User (uid, email, name, created_at)
✅ Module (id, code, title, description, sort_order)
✅ Lesson (id, module_id, title, sort_order)
✅ Sign (id, word, category, video_path, thumb_path, tags)
✅ Quiz (id, module_id, type, title)
✅ QuizQuestion (id, quiz_id, prompt, options, answer)
✅ SignPair (id, word, sign_id) - Para Memory Match
✅ UserModuleProgress (user_id, module_id, percent, last_activity)
✅ QuizAttempt (id, user_id, quiz_id, score, total, duration_ms)
✅ MemoryRun (id, user_id, module_id, matches, attempts, streak, duration_ms)
```
**Análisis:** Todos los modelos según SRS implementados. Relaciones SQLAlchemy correctas.

##### Routers/Endpoints - 90% ✅
```
✅ /users/me - Obtener perfil usuario (requiere auth)
✅ /modules - Listar módulos
✅ /modules/{id} - Detalle módulo (necesita implementar)
✅ /dictionary - Búsqueda con query y categoría
✅ /dictionary/{word} - Detalle de seña (necesita implementar)
✅ /quizzes - Listar quizzes por módulo
✅ /quizzes/{id} - Detalle quiz
✅ /quizzes/attempt - Enviar intento (requiere auth)
✅ /quizzes/my-attempts - Historial (requiere auth)
✅ /memory/deck - Obtener mazo para Memory Match
✅ /memory/attempt - Guardar partida (requiere auth)
✅ /progress - Actualizar progreso (requiere auth)
✅ /progress - Consultar progreso (requiere auth)
✅ /media/video/{sign_id} - URL de video (requiere auth)
⚠️ /stats/summary - NO implementado
⚠️ /stats/monthly - NO implementado
❌ /lessons?module_id= - NO implementado
```

##### Schemas (Pydantic) - 100% ✅
Validación de datos entrada/salida implementada para todos los modelos.

##### CRUD Operations - 90% ✅
```
✅ modules.py - Crear/Listar módulos
✅ dictionary.py - Crear/Buscar señas
✅ quizzes.py - CRUD completo + cálculo de puntaje
✅ memory.py - Generar mazos + guardar partidas
✅ progress.py - Actualizar/consultar progreso
⚠️ media.py - Genera URL firmada (pero Firebase Storage no disponible)
```

##### Autenticación - 70% ⚠️
```
✅ dependencies.py configurado con Firebase Admin SDK
✅ get_current_user() verifica tokens JWT
✅ oauth2_scheme configurado
⚠️ firebase-service-account.json requerido (no presente)
❌ No integrado en app Android (login hardcoded)
```

##### Base de Datos - 85% ✅
```
✅ SQLite configurada (ensenas.db)
✅ 8 módulos populados
✅ 43 señas en diccionario
✅ Tablas creadas automáticamente (SQLAlchemy)
✅ Script populate_db_standalone.py funcional
⚠️ Migración a MySQL pendiente (según SRS)
⚠️ Sin backups automáticos
```

#### ❌ **Componentes Faltantes del Backend**

1. **Endpoints de Estadísticas**
   - GET /stats/summary (precisión global, racha, tiempo total)
   - GET /stats/monthly?yyyy_mm= (agregados mensuales)

2. **Endpoint de Lecciones**
   - GET /lessons?module_id= (contenido de lecciones)

3. **Endpoints Admin** (Opcional MVP)
   - POST/PUT /admin/sign
   - POST/PUT /admin/quiz
   - POST /admin/bulk/signs

4. **Sistema de Almacenamiento Multimedia**
   - **CRÍTICO:** Firebase Storage no disponible
   - Videos/imágenes sin solución de almacenamiento
   - `/media/video/{sign_id}` genera URLs pero sin backend

---

### 2️⃣ FRONTEND (Android/Kotlin + Jetpack Compose)

#### ✅ **Pantallas Implementadas**

##### Navegación - 100% ✅
```
✅ SplashScreen.kt - Pantalla inicial
✅ LoginScreen.kt - Login (UI completa, auth pendiente)
✅ RegisterScreen.kt - Registro (UI completa, auth pendiente)
✅ HomeScreen.kt - Dashboard profesional (680 líneas, estilo Duolingo)
✅ ModulesScreen.kt - Path vertical con animaciones (820 líneas)
✅ DictionaryScreen.kt - Búsqueda de señas (integración API)
✅ DictionaryDetailScreen.kt - Detalle con ExoPlayer
✅ QuizScreen.kt - 4 tipos de quiz (750 líneas)
✅ MemoryGameScreen.kt - Juego Memory Match
✅ AchievementsScreen.kt - Sistema de logros (600 líneas)
✅ LeaderboardScreen.kt - Tabla de clasificación (500 líneas)
✅ ChatBotScreen.kt - Chatbot BorregoBot (200 líneas)
✅ ProfileScreen.kt - Perfil de usuario
```

##### Componentes UI - 95% ✅
```
✅ Bottom Navigation Bar (4 tabs)
✅ Animaciones profesionales (Spring, Pulsing, Rotation)
✅ Material3 Design System
✅ Colores Azul Tec (#0039A6) + Blanco
✅ ExoPlayer integrado para videos
✅ Sistema de gamificación visual (XP, rachas, logros)
✅ Cards con elevación y gradientes
✅ Calendario de racha semanal
⚠️ Borrego animado (placeholder de imagen)
```

##### Data Layer - 60% ⚠️
```
✅ ApiService.kt - Interface Retrofit (12+ endpoints)
✅ RetrofitInstance.kt - Cliente HTTP configurado
✅ Models (SignResponse, ModuleResponse, QuizResponse, etc.)
✅ GamificationRepository.kt - Lógica de gamificación local
✅ ChatRepository.kt - Detección de intents (9 tipos)
❌ Room Database - NO implementado (caché offline)
❌ AuthRepository - NO implementado
❌ UserRepository - NO implementado
⚠️ BASE_URL apunta a localhost (10.0.2.2:8000)
```

##### ViewModels - 40% ⚠️
```
✅ ChatViewModel.kt - Estado del chatbot
⚠️ Otros ViewModels necesarios pero no implementados:
   ❌ AuthViewModel
   ❌ DictionaryViewModel
   ❌ ModulesViewModel
   ❌ QuizViewModel
   ❌ ProfileViewModel
```

#### ❌ **Componentes Faltantes del Frontend**

1. **Autenticación Funcional**
   - Firebase Auth configurado en build.gradle
   - LoginScreen/RegisterScreen son solo UI mockup
   - No hay flujo real de login/registro
   - Tokens no se envían al backend

2. **Integración Backend Real**
   - DictionaryScreen llama API pero manejo de errores básico
   - Otras pantallas usan datos hardcoded
   - No hay manejo de estados de carga/error consistente
   - No hay retry logic ni offline handling

3. **Sistema de Caché (Room)**
   - No hay base de datos local
   - Modo offline no funcional
   - Sin persistencia de progreso local

4. **ViewModels MVVM**
   - Arquitectura MVVM no completada
   - Lógica de negocio mezclada en Composables
   - No hay separación clara de responsabilidades

5. **Reproducción de Videos**
   - ExoPlayer implementado
   - Pero videos apuntan a assets locales (mockup)
   - Sin integración con backend de multimedia

6. **Navegación Profunda**
   - Navegación básica funcional
   - Falta paso de parámetros en algunas rutas
   - Quiz/Memory no reciben moduleId real

---

### 3️⃣ BASE DE DATOS

#### ✅ **Estado Actual - SQLite**
```
✅ Archivo: ensenas.db (en raíz del proyecto)
✅ 10 tablas creadas (según SRS)
✅ 8 módulos insertados:
   - Abecedario (25 letras)
   - Números (10 números)
   - Colores (10 colores)
   - Familia (8 señas)
   - Animales (15 animales)
   - Saludos (10 expresiones)
   - Emociones (8 estados)
   - Comida (12 alimentos)

✅ 43 señas en diccionario con:
   - word, category, video_path, thumb_path
   - Paths apuntan a Firebase Storage (no disponible)

✅ Índices creados:
   - idx_word en signs
   - idx_user_created en quiz_attempts
   - Primary keys compuestas en user_module_progress

⚠️ Migración a MySQL no iniciada
```

#### 📊 **Estructura de Tablas Validada**
Todas las tablas del SRS están creadas y con relaciones correctas:
- users, modules, lessons
- signs, sign_pairs
- quizzes, quiz_questions, quiz_attempts
- user_module_progress, memory_runs

---

### 4️⃣ MULTIMEDIA Y ALMACENAMIENTO

#### ❌ **PROBLEMA CRÍTICO - Firebase Storage No Disponible**

**Situación Actual:**
- SRS requiere Firebase Storage para videos/imágenes
- Firebase Storage requiere plan Blaze (pago)
- Cliente NO puede usar Firebase Storage

**Impacto:**
```
❌ 43 señas sin videos accesibles
❌ /media/video/{sign_id} genera URLs inválidas
❌ DictionaryDetailScreen no puede reproducir videos
❌ Quiz de videos no funcional
❌ Memory Match sin imágenes de señas
```

**Soluciones Propuestas:**

##### Opción 1: Almacenamiento Local en Assets ⭐ RECOMENDADA
```
Pros:
✅ Gratis, funciona offline
✅ Videos incluidos en APK
✅ ExoPlayer ya implementado para assets
✅ No requiere backend

Contras:
⚠️ APK grande (200+ videos = ~500MB-1GB)
⚠️ No escalable (cada actualización requiere nuevo APK)
⚠️ Sin analytics de uso
```

##### Opción 2: Servidor de Archivos Propio
```
Pros:
✅ Control total, gratis (con hosting existente)
✅ Escalable, actualizaciones sin APK

Contras:
⚠️ Requiere servidor con ancho de banda
⚠️ Configuración de CORS
⚠️ No hay CDN (latencia)
```

##### Opción 3: Cloudflare R2 / AWS S3 (Free Tier)
```
Pros:
✅ Similar a Firebase Storage
✅ AWS Free Tier: 5GB storage + 20k requests/mes
✅ Cloudflare R2: Egress gratis

Contras:
⚠️ Requiere tarjeta de crédito
⚠️ Límites de free tier
```

##### Opción 4: Supabase Storage ⭐ ALTERNATIVA RECOMENDADA
```
Pros:
✅ 1GB storage gratis (sin tarjeta)
✅ Compatible con Firebase (similar API)
✅ URLs firmadas incluidas
✅ Dashboard para gestión

Contras:
⚠️ Límite de 1GB (suficiente para MVP con ~50 videos)
⚠️ Requiere migración de código
```

---

### 5️⃣ AUTENTICACIÓN Y SEGURIDAD

#### ⚠️ **Estado Actual - Parcialmente Implementado**

##### Backend
```
✅ Firebase Admin SDK configurado
✅ Verificación de JWT implementada
✅ Endpoints protegidos marcados con Depends(get_current_user)
⚠️ firebase-service-account.json no presente (advertencia en logs)
⚠️ Sin rate limiting
⚠️ Sin HTTPS (desarrollo)
```

##### Frontend
```
✅ Firebase Auth SDK en build.gradle
✅ LoginScreen UI completa
✅ RegisterScreen UI completa
❌ No hay llamadas reales a Firebase Auth
❌ Tokens no se almacenan ni envían
❌ No hay AuthRepository/ViewModel
❌ SplashScreen no verifica sesión activa
```

#### 🚨 **Faltante Crítico**
1. **Flujo de Autenticación Completo**
   - Registro con Firebase Auth
   - Login con Firebase Auth
   - Obtener ID Token
   - Guardar token en DataStore/SharedPreferences
   - Enviar token en headers de Retrofit
   - Refresh token automático

2. **Manejo de Sesiones**
   - Persistir sesión entre cierres de app
   - Logout funcional
   - Navegación condicional (autenticado/no autenticado)

---

### 6️⃣ GAMIFICACIÓN

#### ✅ **Sistema Completo - 100%**
```
✅ GamificationModels.kt - Modelos de datos completos
   - UserLevel (1-50 con XP exponencial)
   - Achievement (25 logros en 7 categorías)
   - Leaderboard (weekly/all-time/friends)
   - StreakData (racha con cálculo de fechas)
   - DailyGoal (meta diaria con progreso)

✅ GamificationRepository.kt - Lógica implementada
   - addXP() con detección de level-up
   - updateStreak() con reset logic
   - updateAchievements() verifica todos los 25
   - Leaderboard ranking

✅ UI Completa
   - AchievementsScreen con 7 categorías
   - LeaderboardScreen con podio
   - HomeScreen con calendario de racha
   - Progress bars animadas
   - Notificaciones de logros

⚠️ NO INTEGRADO CON BACKEND
   - Datos locales en memoria (StateFlow)
   - No se persisten en base de datos
   - No hay sincronización entre dispositivos
```

---

### 7️⃣ QUIZZES

#### ✅ **Backend - 100%**
```
✅ Modelo Quiz con 3 tipos (multiple_choice, complete, pair)
✅ QuizQuestion con prompt, options JSON, answer
✅ QuizAttempt con score, total, duration_ms
✅ CRUD completo en quizzes.py
✅ Cálculo automático de puntaje
✅ Endpoint /quizzes/seed-test-quiz para testing
```

#### ⚠️ **Frontend - 70%**
```
✅ QuizScreen.kt con 4 tipos de quiz
✅ Sistema de vidas (3 corazones)
✅ Sistema de XP (+10 correcto, +50 perfecto)
✅ Timer para Speed Round
✅ QuizResultsScreen con estrellas
✅ Animaciones de feedback

⚠️ Datos hardcoded (no consume API)
❌ No envía intentos al backend
❌ No guarda progreso
```

---

### 8️⃣ MEMORY MATCH

#### ✅ **Backend - 100%**
```
✅ Modelo SignPair (word + sign_id)
✅ GET /memory/deck - Genera mazo aleatorio
✅ POST /memory/attempt - Guarda partida
✅ MemoryRun con matches, attempts, streak, duration_ms
```

#### ⚠️ **Frontend - 60%**
```
✅ MemoryGameScreen.kt implementado
⚠️ Grid de cartas (mockup visual)
❌ No consume API /memory/deck
❌ No envía resultados al backend
❌ Lógica de emparejamiento local
```

---

### 9️⃣ DICCIONARIO LSM

#### ✅ **Backend - 90%**
```
✅ Modelo Sign con video_path, thumb_path, tags JSON
✅ GET /dictionary con búsqueda por prefijo
✅ Filtro por categoría
✅ Paginación (skip, limit)
✅ 43 señas insertadas

⚠️ GET /dictionary/{word} - Router existe pero CRUD no implementado
```

#### ✅ **Frontend - 80%**
```
✅ DictionaryScreen integrada con API
✅ Búsqueda funcional
✅ DictionaryDetailScreen con ExoPlayer
✅ UI profesional con cards

⚠️ Videos apuntan a assets locales
❌ Sin thumbnails (thumb_path no se usa)
❌ Sin categorías visuales
```

---

### 🔟 CHATBOT "BORREGOBOT"

#### ✅ **Implementación Local - 100%**
```
✅ ChatModels.kt - 9 tipos de intents
✅ ChatRepository.kt - Detección de intents por keywords
✅ ChatViewModel.kt - Manejo de estado
✅ MessageBubble.kt - 5 tipos de burbujas
✅ ChatBotScreen.kt - UI completa estilo WhatsApp

✅ Features:
   - Typing indicator animado
   - Quick replies (hasta 3)
   - Video placeholders
   - Menú de opciones
   - Auto-scroll

⚠️ NLP básico (keyword matching)
❌ No integrado con Gemini/GPT-4 (según SRS)
❌ Respuestas templated (no contextuales)
```

---

## 🎯 CUMPLIMIENTO DEL SRS

### ✅ Requisitos Funcionales Implementados

| ID | Requisito | Backend | Frontend | Estado |
|----|-----------|---------|----------|--------|
| RF-001 | Registro Firebase Auth | ⚠️ 70% | ❌ 0% | 35% |
| RF-002 | Login Firebase Auth | ⚠️ 70% | ❌ 0% | 35% |
| RF-003 | Restablecer contraseña | ❌ 0% | ❌ 0% | 0% |
| RF-004 | Catálogo Módulos/Lecciones | ✅ 90% | ✅ 80% | 85% |
| RF-005 | Diccionario | ✅ 90% | ✅ 80% | 85% |
| RF-006 | Quizzes | ✅ 100% | ⚠️ 70% | 85% |
| RF-007 | Memory Match | ✅ 100% | ⚠️ 60% | 80% |
| RF-008 | Progreso | ✅ 100% | ❌ 0% | 50% |
| RF-009 | Estadísticas | ❌ 0% | ✅ 100%* | 50% |
| RF-010 | Multimedia | ⚠️ 50% | ✅ 80% | 65% |
| RF-011 | Admin contenidos | ❌ 0% | N/A | 0% |
| RF-012 | Offline parcial | ❌ 0% | ❌ 0% | 0% |

*Frontend tiene UI pero sin datos del backend

### ⚠️ Requisitos No Funcionales

| Categoría | Requerido | Actual | Cumplimiento |
|-----------|-----------|--------|--------------|
| Seguridad TLS 1.2+ | ✅ | ⚠️ HTTP dev | Producción pendiente |
| JWT verificado | ✅ | ✅ | 100% |
| RBAC | ✅ | ❌ | 0% |
| Rate-limit | ✅ | ❌ | 0% |
| Cifrado en reposo | ✅ | ⚠️ SQLite sin cifrar | 0% |
| Latencia p95 < 300ms | ✅ | ⏱️ No medido | TBD |
| Disponibilidad ≥95% | ✅ | 🔧 Dev only | 0% |
| Backups diarios | ✅ | ❌ | 0% |
| Tests unitarios ≥60% | ✅ | ❌ 0% | 0% |
| Android 10+ | ✅ | ✅ minSdk 24 | 100% |

---

## 🚨 BRECHAS CRÍTICAS

### 1️⃣ **Firebase Storage - BLOQUEANTE** 🔴
- **Problema:** Cliente no puede pagar Firebase Storage
- **Impacto:** 43 señas sin videos, funcionalidad core rota
- **Prioridad:** CRÍTICA
- **Solución:** Implementar Opción 1 (Assets) u Opción 4 (Supabase)

### 2️⃣ **Autenticación No Funcional** 🔴
- **Problema:** Login/Register son solo mockups
- **Impacto:** No hay usuarios reales, endpoints protegidos inaccesibles
- **Prioridad:** CRÍTICA
- **Pasos:**
  1. Implementar AuthRepository con Firebase Auth
  2. Crear AuthViewModel
  3. Integrar en Login/RegisterScreen
  4. Guardar tokens en DataStore
  5. Interceptor en Retrofit para headers

### 3️⃣ **Integración Backend-Frontend Incompleta** 🟡
- **Problema:** Solo DictionaryScreen consume API real
- **Impacto:** 70% de pantallas con datos hardcoded
- **Prioridad:** ALTA
- **Áreas afectadas:**
  - ModulesScreen
  - QuizScreen
  - MemoryGameScreen
  - ProfileScreen
  - HomeScreen (progreso real)

### 4️⃣ **Modo Offline No Implementado** 🟡
- **Problema:** Sin Room, sin caché, sin sincronización
- **Impacto:** App no funciona sin internet
- **Prioridad:** MEDIA (SRS lo marca como MVP)

### 5️⃣ **Estadísticas Backend Faltantes** 🟡
- **Problema:** /stats/summary y /stats/monthly no existen
- **Impacto:** Métricas en frontend no tienen fuente de datos
- **Prioridad:** MEDIA

### 6️⃣ **Testing Inexistente** 🟡
- **Problema:** 0% cobertura de tests
- **Impacto:** No hay garantía de calidad, regresiones probables
- **Prioridad:** MEDIA
- **Requerido:** ≥60% según SRS

---

## 📈 ESTADO DE DESARROLLO

### Por Módulo
```
Backend FastAPI:         ████████░░ 85%
├─ Modelos:             ██████████ 100%
├─ Routers:             █████████░ 90%
├─ CRUD:                █████████░ 90%
├─ Auth:                ███████░░░ 70%
└─ Multimedia:          ████░░░░░░ 40%

Frontend Android:        ███████░░░ 70%
├─ UI Screens:          █████████░ 95%
├─ Navigation:          ██████████ 100%
├─ Components:          █████████░ 95%
├─ Data Layer:          ██████░░░░ 60%
├─ ViewModels:          ████░░░░░░ 40%
└─ Auth:                ░░░░░░░░░░ 0%

Base de Datos:           ████████░░ 85%
├─ Schema:              ██████████ 100%
├─ Población:           ████████░░ 80%
├─ MySQL Migration:     ░░░░░░░░░░ 0%
└─ Backups:             ░░░░░░░░░░ 0%

Multimedia:              ████░░░░░░ 40%
├─ Backend Endpoints:   ███████░░░ 70%
├─ Storage Solution:    ░░░░░░░░░░ 0%
└─ Frontend Player:     ████████░░ 80%

Autenticación:           ████░░░░░░ 35%
├─ Backend JWT:         ███████░░░ 70%
└─ Frontend Flow:       ░░░░░░░░░░ 0%

Testing:                 ░░░░░░░░░░ 0%
```

### Por Feature (SRS)
```
✅ Gamificación:         ██████████ 100% (local only)
✅ Chatbot:              ██████████ 100% (básico)
✅ Quizzes:              ████████░░ 85%
✅ Memory Match:         ████████░░ 80%
✅ Diccionario:          ████████░░ 85%
⚠️ Módulos/Lecciones:    ████████░░ 85%
⚠️ Progreso:             █████░░░░░ 50%
⚠️ Estadísticas:         █████░░░░░ 50%
❌ Auth Completo:        ███░░░░░░░ 35%
❌ Multimedia Storage:   ████░░░░░░ 40%
❌ Offline Mode:         ░░░░░░░░░░ 0%
❌ Admin Panel:          ░░░░░░░░░░ 0%
```

---

## 🛠️ PLAN DE ACCIÓN RECOMENDADO

### 🔴 **FASE 1: CRÍTICOS (1-2 semanas)**

#### 1.1 Resolver Almacenamiento Multimedia
**Decisión requerida:** Elegir entre:
- **Opción A:** Assets locales (rápido, APK grande)
- **Opción B:** Supabase Storage (1GB gratis, escalable)

**Tareas si Opción A:**
1. Organizar videos en `app/src/main/res/raw/`
2. Actualizar populate_db.py para paths locales
3. Modificar DictionaryDetailScreen para assets
4. Testing de reproducción

**Tareas si Opción B:**
1. Crear cuenta Supabase
2. Configurar bucket público para videos
3. Subir 43 videos (categorizar)
4. Actualizar media.py para URLs Supabase
5. Testing de URLs firmadas

**Estimación:** 3-5 días

#### 1.2 Implementar Autenticación Completa
```kotlin
// 1. Crear AuthRepository.kt
class AuthRepository {
    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun logout()
    suspend fun getIdToken(): String?
}

// 2. Crear AuthViewModel.kt
class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {
    val authState: StateFlow<AuthState>
    fun login(email: String, password: String)
    fun register(email: String, password: String)
    fun logout()
}

// 3. Interceptor para Retrofit
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {
        val token = getTokenFromDataStore()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

// 4. Actualizar LoginScreen.kt y RegisterScreen.kt
@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsState()
    
    when (authState) {
        is AuthState.Success -> onLoginSuccess()
        is AuthState.Error -> showError(authState.message)
        is AuthState.Loading -> showLoading()
    }
}
```

**Archivos a crear/modificar:**
- `data/repository/AuthRepository.kt` (nuevo)
- `viewmodels/AuthViewModel.kt` (nuevo)
- `data/api/AuthInterceptor.kt` (nuevo)
- `screens/LoginScreen.kt` (modificar)
- `screens/RegisterScreen.kt` (modificar)
- `screens/SplashScreen.kt` (agregar verificación de sesión)

**Estimación:** 4-6 días

---

### 🟡 **FASE 2: INTEGRACIÓN (2-3 semanas)**

#### 2.1 ViewModels + Repositories para todas las pantallas
```
Crear:
- DictionaryViewModel + DictionaryRepository
- ModulesViewModel + ModulesRepository
- QuizViewModel + QuizRepository
- MemoryViewModel + MemoryRepository
- ProfileViewModel + UserRepository
- StatsViewModel + StatsRepository

Actualizar pantallas para usar ViewModels:
- ModulesScreen.kt
- QuizScreen.kt
- MemoryGameScreen.kt
- HomeScreen.kt (datos reales de progreso)
- ProfileScreen.kt
```

**Estimación:** 8-12 días

#### 2.2 Implementar Endpoints Faltantes
```python
# app/routers/stats.py (nuevo)
@router.get("/summary")
def get_user_stats_summary(current_user: dict = Depends(get_current_user), db: Session = Depends(get_db)):
    # Calcular precisión global, racha, tiempo total
    pass

@router.get("/monthly")
def get_monthly_stats(yyyy_mm: str, current_user: dict = Depends(get_current_user), db: Session = Depends(get_db)):
    # Agregados por mes
    pass

# app/routers/modules.py (modificar)
@router.get("/{module_id}")
def get_module_details(module_id: int, db: Session = Depends(get_db)):
    # Incluir lecciones en la respuesta
    pass

# app/routers/lessons.py (nuevo)
@router.get("/")
def get_lessons(module_id: int, db: Session = Depends(get_db)):
    pass
```

**Estimación:** 3-4 días

#### 2.3 Sincronización de Progreso
```kotlin
// En cada pantalla que modifica progreso:
QuizScreen → Al finalizar → QuizViewModel.submitAttempt()
MemoryGameScreen → Al finalizar → MemoryViewModel.submitRun()
ModulesScreen → Al cambiar módulo → ProgressViewModel.updateProgress()

// Backend guarda en DB
// Frontend actualiza UI inmediatamente (optimistic update)
```

**Estimación:** 3-5 días

---

### 🟢 **FASE 3: MEJORAS (2-3 semanas)**

#### 3.1 Modo Offline con Room
```kotlin
// 1. Agregar dependencias
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// 2. Crear entidades Room
@Entity(tableName = "modules")
data class ModuleEntity(...)

@Entity(tableName = "signs")
data class SignEntity(...)

// 3. Crear DAOs
@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules")
    suspend fun getAllModules(): List<ModuleEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)
}

// 4. Repository con caché
class ModulesRepository(
    private val api: ApiService,
    private val dao: ModuleDao
) {
    suspend fun getModules(): List<Module> {
        return try {
            // Intenta API
            val response = api.getModules()
            if (response.isSuccessful) {
                val modules = response.body()!!
                dao.insertModules(modules.map { it.toEntity() })
                modules
            } else {
                // Fallback a caché
                dao.getAllModules().map { it.toModel() }
            }
        } catch (e: Exception) {
            // Sin internet, usar caché
            dao.getAllModules().map { it.toModel() }
        }
    }
}
```

**Estimación:** 5-7 días

#### 3.2 Testing
```kotlin
// 1. Tests Unitarios (ViewModels)
class AuthViewModelTest {
    @Test
    fun `login with valid credentials should emit Success state`() = runTest {
        // Arrange
        val mockRepo = mock<AuthRepository>()
        whenever(mockRepo.loginWithEmail(any(), any())).thenReturn(Result.success(mockUser))
        val viewModel = AuthViewModel(mockRepo)
        
        // Act
        viewModel.login("test@test.com", "password123")
        
        // Assert
        assertEquals(AuthState.Success(mockUser), viewModel.authState.value)
    }
}

// 2. Tests de Integración (Repositories)
class DictionaryRepositoryTest {
    @Test
    fun `getSigns should return cached data when API fails`() = runTest {
        // Test offline behavior
    }
}

// 3. Tests UI (Compose)
class LoginScreenTest {
    @Test
    fun `clicking login with empty fields shows error`() {
        composeTestRule.setContent {
            LoginScreen(...)
        }
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()
        composeTestRule.onNodeWithText("El email es requerido").assertIsDisplayed()
    }
}
```

**Estimación:** 5-8 días

#### 3.3 Migración a MySQL (Producción)
```python
# 1. Crear cuenta en Aiven/PlanetScale/Railway
# 2. Actualizar .env
DATABASE_URL=mysql+pymysql://user:password@host:port/ensenas

# 3. Modificar database.py
engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    pool_pre_ping=True,  # Verifica conexión antes de usar
    pool_recycle=3600    # Recicla conexiones cada hora
)

# 4. Migrar datos con Alembic
alembic init migrations
alembic revision --autogenerate -m "Initial migration"
alembic upgrade head

# 5. Poblar DB en producción
python populate_db.py
```

**Estimación:** 2-3 días

---

### 🔵 **FASE 4: PULIDO (1-2 semanas)**

#### 4.1 Mejoras UX
- Loading states consistentes
- Error handling con Snackbars
- Pull-to-refresh en listas
- Skeleton loaders
- Animaciones de transición

#### 4.2 Optimizaciones
- ExoPlayer con caché de videos
- Paginación en listas largas
- Image loading con Coil (placeholders, error images)
- Reducir APK size (ProGuard, R8)

#### 4.3 Accesibilidad
- Content descriptions
- Tamaños de toque ≥48dp
- Contraste de colores (WCAG AA)
- Soporte TalkBack

**Estimación:** 7-10 días

---

## 📊 RESUMEN DE PENDIENTES

### Por Prioridad
```
🔴 CRÍTICO (2 semanas)
├─ Multimedia Storage (3-5 días)
└─ Autenticación (4-6 días)

🟡 ALTO (3 semanas)
├─ ViewModels/Repositories (8-12 días)
├─ Endpoints faltantes (3-4 días)
└─ Sincronización progreso (3-5 días)

🟢 MEDIO (3 semanas)
├─ Room offline (5-7 días)
├─ Testing (5-8 días)
└─ MySQL migration (2-3 días)

🔵 BAJO (2 semanas)
├─ UX polish (4-5 días)
├─ Optimizaciones (3-4 días)
└─ Accesibilidad (2-3 días)
```

### Esfuerzo Total Estimado
- **Desarrollo:** 8-10 semanas (2-2.5 meses)
- **Testing + QA:** 1-2 semanas
- **Despliegue:** 3-5 días

**TOTAL:** ~10-13 semanas (2.5-3 meses para MVP completo)

---

## 🎓 RECOMENDACIONES FINALES

### 1️⃣ **Decisión Multimedia URGENTE**
Reunirse con el equipo para decidir:
- ¿Cuántos videos hay realmente? (SRS dice 200+, DB tiene 43)
- ¿Tamaño total estimado?
- ¿Assets locales o Supabase?

### 2️⃣ **Priorizar Auth**
Sin autenticación funcional, muchas features no se pueden testear end-to-end. Esto debería ser la segunda tarea después de multimedia.

### 3️⃣ **Migración Incremental**
No es necesario migrar todo a MySQL de inmediato. SQLite funciona para MVP. Migrar cuando:
- Haya usuarios concurrentes (>10)
- Se necesiten backups automáticos
- Se requiera analítica compleja

### 4️⃣ **Testing desde Ahora**
No dejar testing para el final. Escribir tests conforme se implementan features nuevas. Meta: 30% cobertura en FASE 2, 60% en FASE 3.

### 5️⃣ **Documentación Técnica**
Crear/actualizar:
- API documentation (Swagger ya existe, asegurar que esté actualizada)
- Architecture Decision Records (ADRs)
- Setup guides actualizados
- Deployment runbooks

### 6️⃣ **CI/CD**
Configurar GitHub Actions para:
- Lint (ktlint, pylint)
- Tests automáticos
- Build APK
- Deploy backend (Railway/Render)

---

## 📞 SIGUIENTE PASO INMEDIATO

**Acción requerida del cliente:**

1. **Decisión sobre almacenamiento multimedia** (plazo: 2 días)
   - Revisar opciones propuestas
   - Validar tamaño de videos existentes
   - Aprobar solución (Assets o Supabase)

2. **Priorización de features faltantes** (plazo: 1 semana)
   - ¿Qué es indispensable para el MVP?
   - ¿Qué puede ser v2.0?
   - Timeline esperado de entrega

3. **Recursos disponibles** (plazo: 1 semana)
   - ¿Cuántos desarrolladores?
   - ¿Tiempo dedicación completa o parcial?
   - ¿Fecha límite de presentación/demo?

---

**Preparado por:** GitHub Copilot  
**Contacto:** A través del equipo de desarrollo  
**Última actualización:** 20 de Noviembre, 2025
