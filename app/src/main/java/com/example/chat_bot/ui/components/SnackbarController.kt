package com.example.chat_bot.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Data class para mensajes de Snackbar
 */
data class SnackbarMessage(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val onAction: (() -> Unit)? = null
)

/**
 * Singleton para controlar Snackbars globalmente
 */
object SnackbarController {
    private val _messages = Channel<SnackbarMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()
    
    fun showMessage(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null
    ) {
        _messages.trySend(
            SnackbarMessage(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
                onAction = onAction
            )
        )
    }
    
    fun showError(message: String) {
        showMessage(
            message = message,
            duration = SnackbarDuration.Long
        )
    }
    
    fun showSuccess(message: String) {
        showMessage(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}

/**
 * Composable para observar y mostrar mensajes de Snackbar
 */
@Composable
fun ObserveSnackbarMessages(snackbarHostState: SnackbarHostState) {
    LaunchedEffect(Unit) {
        SnackbarController.messages.collect { snackbarMessage ->
            val result = snackbarHostState.showSnackbar(
                message = snackbarMessage.message,
                actionLabel = snackbarMessage.actionLabel,
                duration = snackbarMessage.duration
            )
            
            // Ejecutar acción si el usuario presionó el botón de acción
            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                snackbarMessage.onAction?.invoke()
            }
        }
    }
}
