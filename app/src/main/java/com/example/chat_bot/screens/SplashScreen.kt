package com.example.chat_bot.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chat_bot.R
import com.example.chat_bot.ui.theme.AzulTec
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Animación del logo
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Simular carga y verificar si usuario está autenticado
    LaunchedEffect(Unit) {
        delay(2500) // 2.5 segundos de splash
        // TODO: Verificar si hay sesión activa con Firebase
        // Por ahora siempre va a login
        onNavigateToLogin()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulTec),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo del Tec (llama azul)
            Image(
                painter = painterResource(id = R.drawable.logo_tec),
                contentDescription = "Logo Tec",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Nombre de la app
            Text(
                text = "EnSeñas",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Aprende Lengua de Señas Mexicana",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Borrego mascota animado
            Image(
                painter = painterResource(id = R.drawable.borrego_normal),
                contentDescription = "Borrego mascota",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
            )
        }
    }
}
