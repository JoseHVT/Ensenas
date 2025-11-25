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
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    authRepository = AuthRepository(),
                    tokenManager = TokenManager(context.applicationContext)
                ) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    tokenManager = TokenManager(context.applicationContext)
                ) as T
            }
            modelClass.isAssignableFrom(ModulesViewModel::class.java) -> {
                ModulesViewModel(
                    tokenManager = TokenManager(context.applicationContext)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
