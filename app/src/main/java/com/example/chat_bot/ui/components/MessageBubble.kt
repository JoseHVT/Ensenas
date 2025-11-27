package com.example.chat_bot.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.data.models.ChatMessage
import com.example.chat_bot.data.models.MessageType
import com.example.chat_bot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * MessageBubble - Burbuja de mensaje del chat
 */
@Composable
fun MessageBubble(
    message: ChatMessage,
    onQuickReplyClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (message.messageType) {
        MessageType.TEXT -> {
            if (message.isFromUser) {
                UserMessageBubble(message = message, modifier = modifier)
            } else {
                BotMessageBubble(message = message, modifier = modifier)
            }
        }
        MessageType.VIDEO -> {
            BotVideoMessageBubble(message = message, modifier = modifier)
        }
        MessageType.QUICK_REPLY -> {
            QuickReplyBubble(
                message = message,
                onReplyClick = onQuickReplyClick,
                modifier = modifier
            )
        }
        MessageType.TYPING_INDICATOR -> {
            TypingIndicatorBubble(modifier = modifier)
        }
        MessageType.SYSTEM -> {
            SystemMessageBubble(message = message, modifier = modifier)
        }
    }
}

/**
 * Burbuja de mensaje del usuario (derecha, azul)
 */
@Composable
private fun UserMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Burbuja de mensaje
            Surface(
                color = AzulTec,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 4.dp
                ),
                modifier = Modifier.bounceIn()
            ) {
                Text(
                    text = message.content,
                    color = Blanco,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            // Timestamp
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = 11.sp,
                color = GrisMedio,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}

/**
 * Burbuja de mensaje del bot (izquierda, gris)
 */
@Composable
private fun BotMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Avatar del bot
        Surface(
            color = AzulClaro,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Bottom)
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
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Burbuja de mensaje
            Surface(
                color = GrisClaro,
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                modifier = Modifier.bounceIn()
            ) {
                Text(
                    text = message.content,
                    color = NegroTexto,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            // Timestamp
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = 11.sp,
                color = GrisMedio,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
            
            // Quick Replies si existen
            if (!message.quickReplies.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                QuickRepliesRow(
                    replies = message.quickReplies,
                    onReplyClick = { /* Handled by parent */ }
                )
            }
        }
    }
}

/**
 * Burbuja de mensaje con video
 */
@Composable
private fun BotVideoMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Avatar del bot
        Surface(
            color = AzulClaro,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Bottom)
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
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Texto del mensaje
            if (message.content.isNotEmpty()) {
                Surface(
                    color = GrisClaro,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ),
                    modifier = Modifier.bounceIn()
                ) {
                    Text(
                        text = message.content,
                        color = NegroTexto,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Video Player con ExoPlayer
            message.videoUrl?.let { videoUrl ->
                Surface(
                    color = NegroFondo,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(250.dp)
                        .height(140.dp)
                        .bounceIn(delay = 200)
                ) {
                    LSMVideoPlayerCompact(
                        videoUrl = videoUrl,
                        modifier = Modifier.fillMaxSize(),
                        autoPlay = false,
                        muted = false
                    )
                }
            }
            
            // Timestamp
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = 11.sp,
                color = GrisMedio,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
            
            // Quick Replies si existen
            if (!message.quickReplies.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                QuickRepliesRow(
                    replies = message.quickReplies,
                    onReplyClick = { /* Handled by parent */ }
                )
            }
        }
    }
}

/**
 * Fila de Quick Replies
 */
@Composable
private fun QuickRepliesRow(
    replies: List<String>,
    onReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        replies.take(3).forEach { reply ->
            OutlinedButton(
                onClick = { onReplyClick(reply) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AzulTec
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.pressAnimation()
            ) {
                Text(
                    text = reply,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Burbuja de Quick Reply
 */
@Composable
private fun QuickReplyBubble(
    message: ChatMessage,
    onReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(48.dp)) // Align with bot messages
        
        QuickRepliesRow(
            replies = message.quickReplies ?: emptyList(),
            onReplyClick = onReplyClick
        )
    }
}

/**
 * Indicador de que el bot está escribiendo
 */
@Composable
private fun TypingIndicatorBubble(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Avatar del bot
        Surface(
            color = AzulClaro,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Bottom)
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
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Burbuja con puntos animados
        Surface(
            color = GrisClaro,
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    TypingDot(delay = index * 150)
                }
            }
        }
    }
}

/**
 * Punto animado del typing indicator
 */
@Composable
private fun TypingDot(
    delay: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Surface(
        color = GrisMedio,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .size(8.dp)
            .alpha(alpha)
    ) {}
}

/**
 * Mensaje de sistema (centrado, sin burbuja)
 */
@Composable
private fun SystemMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = message.content,
            color = GrisMedio,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(GrisClaro.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * Formatear timestamp a formato HH:mm
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
