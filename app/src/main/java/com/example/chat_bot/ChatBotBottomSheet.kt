package com.example.chat_bot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pantallas internas del chatbot: menú o respuesta
sealed class ChatBotScreenState {
    object Menu : ChatBotScreenState()
    data class Answer(val option: ChatBotOption) : ChatBotScreenState()
}

@Composable
fun ChatBotBottomSheet(
    onClose: () -> Unit
) {
    var screenState by remember { mutableStateOf<ChatBotScreenState>(ChatBotScreenState.Menu) }

    when (val state = screenState) {
        is ChatBotScreenState.Menu -> {
            ChatBotMenu(
                onOptionClick = { option ->
                    screenState = ChatBotScreenState.Answer(option)
                },
                onClose = onClose
            )
        }
        is ChatBotScreenState.Answer -> {
            ChatBotAnswer(
                option = state.option,
                onBackToMenu = { screenState = ChatBotScreenState.Menu }
            )
        }
    }
}

@Composable
private fun ChatBotMenu(
    onOptionClick: (ChatBotOption) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = ChatBotContent.menuTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ChatBotContent.options) { option ->
                Button(
                    onClick = { onOptionClick(option) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = option.label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar el chatbot completo
        OutlinedButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Cerrar chatbot ✖️")
        }
    }
}

@Composable
private fun ChatBotAnswer(
    option: ChatBotOption,
    onBackToMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = option.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = option.body,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onBackToMenu,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "🔙 Regresar al menú")
        }
    }
}
