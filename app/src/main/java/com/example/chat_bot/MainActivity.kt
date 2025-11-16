package com.example.chat_bot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chat_bot.ui.theme.Chat_BotTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Chat_BotTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ChatBotHomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotHomeScreen() {
    // Estado del bottom sheet
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Contenido de la pantalla (por ahora en blanco / placeholder)
        Text(
            text = "Pantalla de EnSeñas (aún en desarrollo)",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodyLarge
        )

        // Burbuja flotante del chatbot
        FloatingActionButton(
            onClick = { isSheetOpen = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(text = "🤖")
        }

        // BottomSheet del chatbot
        if (isSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                ChatBotBottomSheet(
                    onClose = { isSheetOpen = false }
                )
            }
        }
    }
}
