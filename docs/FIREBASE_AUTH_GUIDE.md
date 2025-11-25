# 🔐 Guía de Autenticación Firebase para Enseñas

## 📋 Resumen

La aplicación Enseñas usa **Firebase Authentication** para el manejo de usuarios. Esta guía explica cómo funciona la autenticación y cómo usarla.

---

## 🏗️ Arquitectura de Autenticación

### **Componentes Principales**

```
┌─────────────────┐
│  LoginScreen    │ ← UI Layer (Compose)
│  RegisterScreen │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  AuthViewModel  │ ← ViewModel (Estado)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  AuthRepository │ ← Repository (Lógica)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Firebase Auth  │ ← Firebase SDK
└─────────────────┘
```

### **Flujo de Datos**

1. **Usuario ingresa credenciales** en LoginScreen/RegisterScreen
2. **ViewModel procesa** la acción y llama al Repository
3. **Repository comunica** con Firebase Authentication
4. **Firebase valida** credenciales y retorna token JWT
5. **Token se guarda** en DataStore (TokenManager)
6. **AuthInterceptor añade** el token a todas las peticiones HTTP backend

---

## 🔧 Configuración Actual

### **1. Firebase Project**
Tu proyecto Firebase ya está configurado según la captura:
- **Proyecto**: EnSenas
- **Método de acceso**: Correo electrónico/contraseña ✅ Habilitado

### **2. Archivos de Configuración**
- ✅ `google-services.json` en `app/` (configurado)
- ✅ Firebase BOM en `build.gradle.kts`
- ✅ Firebase Auth SDK instalado

### **3. Dependencias**
```kotlin
// Firebase BOM
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
```

---

## 🚀 Cómo Funciona el Login/Signup

### **1. Registro de Usuario (Sign Up)**

**Flujo:**
```kotlin
// En RegisterScreen
authViewModel.signUp(
    email = "usuario@example.com",
    password = "password123",
    displayName = "Juan Pérez"
)

// AuthViewModel procesa
fun signUp(email: String, password: String, displayName: String) {
    viewModelScope.launch {
        val result = authRepository.signUp(email, password, displayName)
        result.fold(
            onSuccess = { user ->
                // Usuario creado, guardar token
                tokenManager.saveAuthToken(token)
                _authState.value = AuthState.Authenticated
            },
            onFailure = { exception ->
                _errorMessage.value = "Error: ${exception.message}"
            }
        )
    }
}
```

**Lo que sucede en Firebase:**
1. Firebase crea una nueva cuenta con email/password
2. Se actualiza el perfil con `displayName`
3. Firebase genera un **token JWT** único
4. El token se guarda localmente en DataStore

---

### **2. Inicio de Sesión (Sign In)**

**Flujo:**
```kotlin
// En LoginScreen
authViewModel.signIn(
    email = "usuario@example.com",
    password = "password123"
)

// AuthRepository comunica con Firebase
suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
    return try {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(authResult.user!!)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

**Lo que sucede:**
1. Firebase valida email + password
2. Si son correctos, retorna el usuario + token
3. Token se guarda en DataStore para persistencia
4. App navega a HomeScreen

---

### **3. Token JWT y Backend**

**¿Qué es el Token?**
- Es un **JSON Web Token (JWT)** generado por Firebase
- Contiene información del usuario encriptada
- Se renueva automáticamente (válido ~1 hora)

**Cómo se usa con el Backend:**

```kotlin
// AuthInterceptor añade el token automáticamente
override fun intercept(chain: Interceptor.Chain): Response {
    val token = authRepository.getAuthToken() // Obtiene token de Firebase
    
    val authenticatedRequest = originalRequest.newBuilder()
        .header("Authorization", "Bearer $token")
        .build()
    
    return chain.proceed(authenticatedRequest)
}
```

**En el Backend (FastAPI):**
```python
# dependencies.py
async def get_current_user(token: str = Depends(oauth2_scheme)):
    # Firebase Admin SDK valida el token
    decoded_token = auth.verify_id_token(token)
    return decoded_token
```

---

## 🔄 Estados de Autenticación

```kotlin
sealed class AuthState {
    object Loading : AuthState()          // Procesando login/signup
    object Authenticated : AuthState()    // Usuario logueado
    object Unauthenticated : AuthState()  // Sin sesión
    data class Error(val message: String) : AuthState() // Error
}
```

**Uso en UI:**
```kotlin
val authState by authViewModel.authState.collectAsState()

when (authState) {
    is AuthState.Loading -> CircularProgressIndicator()
    is AuthState.Authenticated -> navController.navigate("home")
    is AuthState.Unauthenticated -> { /* Mostrar login */ }
    is AuthState.Error -> Text("Error: ${authState.message}")
}
```

---

## 📝 Implementación en Pantallas

### **LoginScreen (Pendiente de integrar)**

```kotlin
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Observar estado de autenticación
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }
    
    // UI con TextField para email/password
    Button(
        onClick = {
            authViewModel.signIn(email, password)
        },
        enabled = authState !is AuthState.Loading
    ) {
        if (authState is AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            Text("Iniciar Sesión")
        }
    }
}
```

### **RegisterScreen (Pendiente de integrar)**

Similar a LoginScreen, pero llama a `signUp()`:
```kotlin
Button(onClick = {
    authViewModel.signUp(email, password, displayName)
}) {
    Text("Registrarse")
}
```

---

## 🔒 Seguridad

### **1. Token Storage**
- ✅ Tokens se guardan en **DataStore** (encriptado)
- ✅ No se guardan en SharedPreferences (menos seguro)
- ✅ Token se limpia al hacer logout

### **2. Validación Backend**
```python
# El backend valida CADA petición con el token
@router.get("/progress")
def get_my_progress(
    current_user: dict = Depends(get_current_user),  # ← Requiere token válido
    db: Session = Depends(get_db)
):
    return crud_progress.get_user_progress(db, user_id=current_user["uid"])
```

### **3. Renovación Automática**
Firebase renueva tokens automáticamente. El `AuthInterceptor` siempre obtiene el token más reciente.

---

## 🛠️ Siguientes Pasos

### **Fase 7.2: Integrar AuthViewModel en Login/RegisterScreen**
1. Añadir `AuthViewModel` a LoginScreen
2. Conectar botones de login con `authViewModel.signIn()`
3. Observar `authState` para navegar al home
4. Mostrar errores de autenticación

### **Fase 7.3: Integrar Backend en HomeScreen**
1. Crear `HomeViewModel` 
2. Llamar `api.getUserStats(token)` 
3. Reemplazar datos mock con datos reales

### **Fase 7.4: Proteger Rutas**
1. Verificar autenticación en navegación
2. Redirigir a login si no hay sesión
3. Implementar auto-logout en 401/403

---

## 📚 Recursos

- [Firebase Auth Docs](https://firebase.google.com/docs/auth)
- [Firebase Admin Python SDK](https://firebase.google.com/docs/admin/setup)
- [JWT.io - Debugger de Tokens](https://jwt.io)

---

## ⚠️ Notas Importantes

1. **El token JWT expira en ~1 hora** - Firebase lo renueva automáticamente
2. **Cada endpoint protegido** en el backend requiere el token
3. **AuthInterceptor añade el token automáticamente** - no necesitas hacerlo manualmente
4. **Logout limpia DataStore** - no quedan tokens residuales

---

**Estado Actual**: ✅ Infraestructura completa  
**Próximo Paso**: Integrar AuthViewModel en Login/RegisterScreen
