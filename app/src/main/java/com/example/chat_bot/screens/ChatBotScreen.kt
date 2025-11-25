package com.example.chat_bot.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import com.example.chat_bot.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

/**
 * ChatBotScreen - Pantalla principal del chatbot
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll al último mensaje cuando se agrega uno nuevo
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    // Estado del menú
    var showMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar del bot con animación
                        BouncingIcon(
                            modifier = Modifier.size(40.dp),
                            content = {
                                Surface(
                                    color = AzulClaro,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🐏",
                                            fontSize = 24.sp
                                        )
                                    }
                                }
                            }
                        )
                        
                        // Nombre y estado
                        Column {
                            Text(
                                text = "BorregoBot",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NegroTexto
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isTyping) {
                                    PulsingDot(color = AzulTec, size = 8.dp)
                                } else {
                                    PulsingDot(color = VerdeExito, size = 8.dp)
                                }
                                Text(
                                    text = if (isTyping) "escribiendo..." else "En línea",
                                    fontSize = 12.sp,
                                    color = if (isTyping) AzulTec else VerdeExito
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = NegroTexto
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = NegroTexto
                        )
                    }
                    
                    // Dropdown Menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Limpiar conversación") },
                            onClick = {
                                viewModel.clearConversation()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver perfil del bot") },
                            onClick = {
                                // TODO: Navigate to bot profile
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ayuda") },
                            onClick = {
                                viewModel.sendMessage("Ayuda")
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blanco
                )
            )
        },
        bottomBar = {
            ChatInput(
                text = inputText,
                onTextChange = { viewModel.updateInputText(it) },
                onSendMessage = { viewModel.sendMessage() },
                enabled = !isTyping
            )
        },
        containerColor = BlancoFondo,
        modifier = modifier
    ) { paddingValues ->
        // Lista de mensajes
        if (messages.isEmpty()) {
            // Estado vacío (solo se muestra brevemente antes de cargar mensajes de bienvenida)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AzulTec
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    var isVisible by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(Unit) {
                        isVisible = true
                    }
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(300)) + 
                                slideInVertically(
                                    initialOffsetY = { it / 3 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                    ) {
                        MessageBubble(
                            message = message,
                            onQuickReplyClick = { reply ->
                                viewModel.handleQuickReply(reply)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Preview del ChatBotScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ChatBotScreenPreview() {
    EnsenasTheme {
        ChatBotScreen(
            onNavigateBack = {}
        )
    }
}
