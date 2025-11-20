# 📱 EnSeñas - Aprende Lengua de Señas Mexicana

<div align="center">

![Azul Tec](https://img.shields.io/badge/Tec_de_Monterrey-%230039A6?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)

**Aplicación móvil educativa para el aprendizaje de LSM inspirada en Duolingo**

[Características](#-características) • [Instalación](#-instalación) • [Tecnologías](#%EF%B8%8F-tecnologías) • [Desarrollo](#-desarrollo)

</div>

---

## 🎯 Descripción

**EnSeñas** es una aplicación móvil Android que facilita el aprendizaje de la Lengua de Señas Mexicana (LSM) mediante gamificación, videos interactivos, quizzes personalizados y un chatbot asistente con reconocimiento de señas por IA.

### 🌟 Características Principales

- 🎥 **200+ Videos LSM** - Abecedario, números, colores, animales y más
- 🧩 **8 Módulos Estructurados** - Aprendizaje progresivo y adaptativo
- 🏆 **Sistema de Gamificación** - XP, rachas, logros y niveles
- 🎯 **4 Tipos de Quizzes** - Práctica variada y efectiva
- 🤖 **Chatbot Inteligente** - Asistente LSM con reconocimiento de gestos
- 🎮 **Juego de Memoria** - Refuerzo lúdico del aprendizaje
- 📊 **Seguimiento de Progreso** - Estadísticas detalladas
- 🌙 **Modo Oscuro** - Comodidad visual
- ✈️ **Modo Offline** - Aprende sin conexión

---

## 🏗️ Arquitectura

```
┌──────────────────────────────────────────┐
│      ANDROID APP (Jetpack Compose)       │
│  ┌────────────────────────────────────┐  │
│  │ UI Layer: 8 Screens                │  │
│  │ Navigation + BottomBar              │  │
│  └────────────────────────────────────┘  │
│             ↓ ViewModel                  │
│  ┌────────────────────────────────────┐  │
│  │ Data Layer: Repository Pattern      │  │
│  │ Retrofit API + Room Cache           │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
                ↓ REST API
┌──────────────────────────────────────────┐
│       BACKEND (FastAPI + Python)         │
│  ┌────────────────────────────────────┐  │
│  │ 7 Routers: dictionary, modules,     │  │
│  │ users, quizzes, progress, memory    │  │
│  └────────────────────────────────────┘  │
│             ↓ SQLAlchemy ORM             │
│  ┌────────────────────────────────────┐  │
│  │ Database: SQLite → MySQL            │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

---

## 🛠️ Tecnologías

### Frontend (Android)
- **Kotlin** + **Jetpack Compose** - UI moderna y declarativa
- **Material3** - Design system con Azul Tec #0039A6
- **Navigation Compose** - Navegación type-safe
- **ExoPlayer** - Reproducción de videos LSM
- **Retrofit** - Cliente REST API
- **Firebase Auth** - Autenticación de usuarios
- **Room** - Cache local (offline mode)
- **Coil** - Carga de imágenes optimizada

### Backend
- **FastAPI** - Framework web asíncrono
- **SQLAlchemy** - ORM para base de datos
- **Pydantic** - Validación de datos
- **SQLite** (desarrollo) / **MySQL** (producción)

### IA y ML
- **ML Kit** - Reconocimiento de gestos (futuro)
- **TensorFlow Lite** - Modelo de clasificación LSM (futuro)

---

## 📦 Instalación

### Requisitos Previos
- **Android Studio** Otter 2025.2.1+
- **JDK 21** (jbr-21)
- **Python 3.13+**
- **Git**

### 1️⃣ Clonar el Repositorio
```bash
git clone https://github.com/JoseHVT/Ensenas.git
cd Ensenas
```

### 2️⃣ Configurar Backend
```bash
# Instalar dependencias
pip install -r requirements.txt

# Poblar base de datos
python populate_db_standalone.py

# Iniciar servidor (http://localhost:8000)
python start_server.py
```

**Verificar API:** Abre http://localhost:8000/docs

### 3️⃣ Configurar Android App
1. Abrir `Ensenas/` en Android Studio
2. **Sync Gradle** (esperar 2-3 minutos)
3. Conectar emulador o dispositivo físico
4. **Run 'app'** ▶️

---

## 📂 Estructura del Proyecto

```
Ensenas/
├── app/                              # Android App
│   ├── src/main/
│   │   ├── java/com/example/chat_bot/
│   │   │   ├── screens/              # 8 Pantallas Compose
│   │   │   ├── navigation/           # NavHost + BottomBar
│   │   │   ├── ui/theme/             # Colores y tipografía
│   │   │   ├── data/
│   │   │   │   ├── api/              # RetrofitInstance
│   │   │   │   └── models/           # Data classes
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── drawable/             # Borrego mascota
│   │   │   └── raw/                  # Videos LSM (.m4v)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── app/ (Python)                     # Backend FastAPI
│   ├── routers/                      # 7 endpoints
│   ├── crud/                         # Database operations
│   ├── models.py                     # SQLAlchemy models
│   └── main.py
├── ensenas.db                        # SQLite (43 señas, 8 módulos)
├── requirements.txt
├── start_server.py
├── PLAN_DESARROLLO_COMPLETO.md       # Roadmap completo
└── README.md
```

---

## 🚀 Desarrollo

### Estado Actual (40% completado)

#### ✅ Completado
- 8 pantallas navegables (Splash, Login, Register, Home, Modules, Dictionary, Profile, DictionaryDetail)
- Sistema de navegación con BottomBar (4 tabs)
- Tema personalizado Azul Tec + Material3
- Reproductor de video con ExoPlayer
- Backend FastAPI con 7 routers operacionales
- Base de datos SQLite poblada (43 señas, 8 módulos)
- Integración Retrofit con fallback local

#### 🚧 En Progreso
- Rediseño UI/UX profesional estilo Duolingo
- HomeScreen con dashboard mejorado

#### ⏳ Pendiente
- QuizScreen (4 tipos de preguntas)
- MemoryGameScreen
- Chatbot con ML
- Sistema completo de gamificación
- Migración a MySQL
- Testing automatizado

### Roadmap Completo
Ver: **[PLAN_DESARROLLO_COMPLETO.md](./PLAN_DESARROLLO_COMPLETO.md)**

---

## 🎨 Design System

### Colores Principales
- **Azul Tec:** `#0039A6` (Primary)
- **Verde Éxito:** `#58CC02` (Duolingo-inspired)
- **Rojo Error:** `#FF4B4B`
- **Amarillo Advertencia:** `#FFC800`

### Tipografía
- Display Large: 32sp / ExtraBold
- Headline Large: 28sp / Bold
- Title Large: 22sp / Bold
- Body Large: 16sp / Normal

---

## 🧪 Testing

### Backend
```bash
# Probar endpoints
curl http://localhost:8000/dictionary
curl http://localhost:8000/modules
```

### Android
```bash
# Desde Android Studio
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

---

## 📚 Documentación Adicional

- **[PLAN_DESARROLLO_COMPLETO.md](./PLAN_DESARROLLO_COMPLETO.md)** - Plan de desarrollo detallado con 8 fases

---

## 🤝 Contribución

1. Fork el repositorio
2. Crea una branch feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'Add: nueva funcionalidad'`
4. Push a la branch: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 👥 Equipo

**Desarrollador:** [JoseHVT](https://github.com/JoseHVT)  
**Institución:** Tec de Monterrey  
**Branch Actual:** `Chat_Bot_Dev`

---

## 📄 Licencia

Este proyecto es parte de un proyecto académico del Tec de Monterrey.

---

## 📞 Soporte

**Repositorio:** https://github.com/JoseHVT/Ensenas  
**Issues:** https://github.com/JoseHVT/Ensenas/issues

---

<div align="center">

**Hecho con ❤️ para la comunidad sorda mexicana**

</div>
