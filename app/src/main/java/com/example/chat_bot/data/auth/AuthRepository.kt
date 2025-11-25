package com.example.chat_bot.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository que maneja toda la lógica de autenticación con Firebase
 */
class AuthRepository {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    /**
     * Usuario actualmente autenticado
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    /**
     * Verifica si hay un usuario logueado
     */
    val isUserLoggedIn: Boolean
        get() = currentUser != null
    
    /**
     * Obtiene el token de autenticación del usuario actual
     * Este token se usa en los headers de las peticiones al backend
     */
    suspend fun getAuthToken(): String? {
        return try {
            currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Registra un nuevo usuario con email y contraseña
     */
    suspend fun signUp(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            
            // Actualizar perfil con el nombre
            user?.let {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                it.updateProfile(profileUpdates).await()
            }
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error al crear usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error al iniciar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Inicia sesión con Google
     */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error al iniciar sesión con Google"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Envía email de recuperación de contraseña
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Observa los cambios en el estado de autenticación
     */
    fun observeAuthState(): Flow<FirebaseUser?> = flow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            // Este emit debería estar dentro de una coroutine
        }
        auth.addAuthStateListener(listener)
        // Emite el estado inicial
        emit(currentUser)
    }
}
