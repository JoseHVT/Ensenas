package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.auth.AuthRepository
import com.example.chat_bot.data.auth.AuthState
import com.example.chat_bot.data.auth.TokenManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja el estado de autenticación de la aplicación
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    // Estado de autenticación
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // Usuario actual
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    // Mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    /**
     * Verifica el estado de autenticación al iniciar
     */
    private fun checkAuthState() {
        viewModelScope.launch {
            val user = authRepository.currentUser
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
                // Guardar token
                authRepository.getAuthToken()?.let { token ->
                    tokenManager.saveAuthToken(token)
                    tokenManager.saveUserInfo(
                        userId = user.uid,
                        email = user.email,
                        displayName = user.displayName
                    )
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    /**
     * Registra un nuevo usuario
     */
    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _errorMessage.value = null
            
            val result = authRepository.signUp(email, password, displayName)
            
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                    
                    // Guardar token y datos del usuario
                    authRepository.getAuthToken()?.let { token ->
                        tokenManager.saveAuthToken(token)
                        tokenManager.saveUserInfo(
                            userId = user.uid,
                            email = user.email,
                            displayName = user.displayName
                        )
                    }
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error desconocido")
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
        }
    }
    
    /**
     * Inicia sesión con email y contraseña
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _errorMessage.value = null
            
            val result = authRepository.signIn(email, password)
            
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                    
                    // Guardar token y datos del usuario
                    authRepository.getAuthToken()?.let { token ->
                        tokenManager.saveAuthToken(token)
                        tokenManager.saveUserInfo(
                            userId = user.uid,
                            email = user.email,
                            displayName = user.displayName
                        )
                    }
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error desconocido")
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
        }
    }
    
    /**
     * Inicia sesión con Google
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _errorMessage.value = null
            
            val result = authRepository.signInWithGoogle(idToken)
            
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                    
                    // Guardar token y datos del usuario
                    authRepository.getAuthToken()?.let { token ->
                        tokenManager.saveAuthToken(token)
                        tokenManager.saveUserInfo(
                            userId = user.uid,
                            email = user.email,
                            displayName = user.displayName
                        )
                    }
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Error desconocido")
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
        }
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            tokenManager.clearAll()
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    /**
     * Envía email de recuperación de contraseña
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            val result = authRepository.sendPasswordResetEmail(email)
            
            result.fold(
                onSuccess = {
                    _errorMessage.value = "Email de recuperación enviado"
                },
                onFailure = { exception ->
                    _errorMessage.value = getErrorMessage(exception)
                }
            )
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Convierte excepciones de Firebase en mensajes amigables
     */
    private fun getErrorMessage(exception: Throwable): String {
        return when (exception.message) {
            "The email address is badly formatted." -> 
                "El formato del correo electrónico no es válido"
            "The password is invalid or the user does not have a password." -> 
                "Contraseña incorrecta"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> 
                "No existe una cuenta con este correo"
            "The email address is already in use by another account." -> 
                "Este correo ya está registrado"
            "Password should be at least 6 characters" -> 
                "La contraseña debe tener al menos 6 caracteres"
            else -> exception.message ?: "Error desconocido"
        }
    }
}
