package com.example.chat_bot.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chat_bot.data.auth.AuthRepository
import com.example.chat_bot.data.auth.TokenManager

/**
 * Factory para crear ViewModels con dependencias
 */
class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    // Instancias compartidas
    private val tokenManager by lazy { TokenManager(context.applicationContext) }
    private val authRepository by lazy { AuthRepository() }
    
    // AuthViewModel compartido entre todos los ViewModels
    private val authViewModel by lazy { 
        AuthViewModel(
            authRepository = authRepository,
            tokenManager = tokenManager
        )
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                authViewModel as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                ) as T
            }
            modelClass.isAssignableFrom(ModulesViewModel::class.java) -> {
                ModulesViewModel(
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                ) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                ) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    authRepository = authRepository,
                    tokenManager = tokenManager
                ) as T
            }
            modelClass.isAssignableFrom(AchievementsViewModel::class.java) -> {
                AchievementsViewModel(
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                ) as T
            }
            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) -> {
                LeaderboardViewModel(
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                ) as T
            }
            modelClass.isAssignableFrom(MemoryGameViewModel::class.java) -> {
                MemoryGameViewModel(
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
