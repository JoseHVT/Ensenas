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
import androidx.navigation.NavController
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.AzulTec
import com.example.chat_bot.ui.theme.BlancoTec
import com.example.chat_bot.ui.theme.VerdeExito
import com.example.chat_bot.utils.VideoPathMapper
import kotlinx.coroutines.launch

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
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Obtener información de la palabra usando VideoPathMapper
    val videoPath = VideoPathMapper.getFullVideoPath(signWord)
    val hasVideo = VideoPathMapper.hasVideo(signWord)
    
    // Datos específicos según la palabra
    val signDetail = remember(signWord) {
        val definition = when(signWord.lowercase()) {
            "amarillo" -> "Color primario brillante como el sol."
            "azul" -> "Color del cielo y el mar."
            "rojo" -> "Color intenso como la sangre o el fuego."
            "verde" -> "Color de la naturaleza y las plantas."
            "hola" -> "Saludo cordial para iniciar una conversación."
            "buenos días", "buenos dias" -> "Saludo matutino formal."
            "mamá", "mama" -> "Madre, progenitora femenina."
            "papá", "papa" -> "Padre, progenitor masculino."
            "gato" -> "Animal doméstico felino."
            "perro" -> "Animal doméstico canino, mejor amigo del hombre."
            "ratón", "raton" -> "Pequeño roedor."
            else -> "Seña en Lengua de Señas Mexicana (LSM)."
        }
        
        val examples = when(signWord.lowercase()) {
            "hola" -> listOf(
                "¡Hola! ¿Cómo estás?",
                "Hola, mucho gusto en conocerte",
                "Hola, buenos días"
            )
            "buenos días", "buenos dias" -> listOf(
                "Buenos días, ¿cómo amaneciste?",
                "Buenos días a todos",
                "Te deseo buenos días"
            )
            "mamá", "mama" -> listOf(
                "Mi mamá me ayuda con la tarea",
                "Mamá, ¿puedo salir a jugar?",
                "Le dije a mamá que la quiero mucho"
            )
            "amarillo" -> listOf(
                "El sol es amarillo",
                "Mi color favorito es el amarillo",
                "Las flores amarillas son hermosas"
            )
            "mamá", "mama" -> listOf(
                "Mi mamá me quiere mucho",
                "Mamá cocina delicioso",
                "Mamá me ayuda con la tarea"
            )
            "papá", "papa" -> listOf(
                "Papá trabaja muy duro",
                "Mi papá me lleva a la escuela",
                "Papá juega conmigo"
            )
            "azul" -> listOf(
                "El cielo es azul",
                "Me gusta el azul marino",
                "Tengo una camisa azul"
            )
            "gato" -> listOf(
                "Tengo un gato en casa",
                "El gato juega con la pelota",
                "Mi gato se llama Michi"
            )
            else -> listOf(
                "Uso en conversación cotidiana",
                "Práctica con señas relacionadas",
                "Integración en oraciones completas"
            )
        }
        
        val category = when {
            videoPath.contains("Colores") -> "Colores"
            videoPath.contains("Animales") -> "Animales"
            videoPath.contains("Saludos") -> "Saludos y Cortesías"
            videoPath.contains("Personas") -> "Personas y Familia"
            videoPath.contains("Numero") -> "Números"
            videoPath.contains("Abecedario") -> "Abecedario"
            else -> "General"
        }
        
        SignDetail(
            word = signWord,
            category = category,
            videoPath = videoPath,
            definition = definition,
            examples = examples,
            relatedSigns = emptyList()
        )
    }
    
    // Cargar video desde assets
    val videoFileName = when(signWord.lowercase()) {
        "hola" -> "hola.m4v"
        "adios", "adiós" -> "Adios.m4v"
        "buenos dias", "buenos días" -> "buenos_dias.m4v"
        "buenas noches" -> "buenas_noches.m4v"
        "gracias" -> "Gracias.m4v"
        "mama", "mamá" -> "mama.m4v"
        "papa", "papá" -> "papa.m4v"
        "amarillo" -> "amarillo.m4v"
        "azul" -> "azul.m4v"
        "rojo" -> "rojo.m4v"
        else -> "hola.m4v" // video por defecto
    }
    val videoUri = "file:///android_asset/videos/$videoFileName"
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
            // Video Player con LSMVideoPlayer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(16f / 9f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LSMVideoPlayer(
                    videoUrl = videoUri,
                    autoPlay = false,
                    loop = true,
                    showControls = true,
                    modifier = Modifier.fillMaxSize(),
                    onError = { errorMsg ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Error al reproducir video: $errorMsg",
                                duration = SnackbarDuration.Short
                            )
                        }
                        android.util.Log.e("DictionaryDetail", "Error video: $errorMsg")
                    }
                )
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
