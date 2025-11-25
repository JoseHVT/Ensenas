package com.example.chat_bot.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.AzulTec
import com.example.chat_bot.ui.theme.BlancoTec
import com.example.chat_bot.ui.theme.VerdeExito

data class SignDetail(
    val word: String,
    val category: String,
    val videoPath: String,
    val definition: String = "",
    val examples: List<String> = emptyList(),
    val relatedSigns: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryDetailScreen(
    navController: NavController,
    signWord: String
) {
    val context = LocalContext.current
    
    // Mock data - esto se reemplazará con datos del backend
    val signDetail = remember {
        SignDetail(
            word = signWord,
            category = "Colores",
            videoPath = "amarillo_web", // Sin extensión, ExoPlayer la busca automáticamente
            definition = "Color primario que se obtiene mezclando rojo y verde en el espectro de luz.",
            examples = listOf(
                "El sol es amarillo",
                "Mi camiseta favorita es amarilla",
                "Las flores de este jardín son amarillas"
            ),
            relatedSigns = listOf("Naranja", "Dorado", "Limón")
        )
    }
    
    // ExoPlayer setup
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = Uri.parse("android.resource://${context.packageName}/raw/${signDetail.videoPath}")
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE // Loop el video
        }
    }
    
    var isPlaying by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        signDetail.word,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Regresar", tint = BlancoTec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = BlancoTec
                )
            )
        }
    ) { paddingValues ->
        var isVisible by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            isVisible = true
        }
        
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF8F9FA))
            ) {
            // Video Player
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(16f / 9f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false // Usamos controles personalizados
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Play/Pause Button Overlay con animación
                    Box(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        BouncingIcon(
                            modifier = Modifier.size(64.dp),
                            content = {
                                IconButton(
                                    onClick = {
                                        if (isPlaying) {
                                            exoPlayer.pause()
                                        } else {
                                            exoPlayer.play()
                                        }
                                        isPlaying = !isPlaying
                                    },
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            AzulTec.copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(32.dp)
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                                        tint = BlancoTec,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
            
            // Category Badge
            Surface(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                color = VerdeExito.copy(alpha = 0.2f)
            ) {
                Text(
                    text = signDetail.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = VerdeExito,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Definition Card con gradiente profesional
            if (signDetail.definition.isNotEmpty()) {
                GradientCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    gradient = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            AzulTec.copy(alpha = 0.03f)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PulsingDot(color = AzulTec, size = 8.dp)
                            Text(
                                text = "Definición",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AzulTec
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = signDetail.definition,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Examples Card
            if (signDetail.examples.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ejemplos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulTec
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        signDetail.examples.forEach { example ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VerdeExito,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = example,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Related Signs
            if (signDetail.relatedSigns.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Señas relacionadas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulTec
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            signDetail.relatedSigns.forEach { relatedSign ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AzulTec.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = relatedSign,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AzulTec,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }
        }
    }
}
