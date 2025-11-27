package com.example.chat_bot.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import com.example.chat_bot.viewmodels.ModulesViewModel
import com.example.chat_bot.viewmodels.ModuleWithProgress
import com.example.chat_bot.viewmodels.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ============================================
// DATA MODELS
// ============================================

data class LearningModule(
    val id: Int,
    val title: String,
    val category: ModuleCategory,
    val lessonsCount: Int,
    val completedCount: Int,
    val isLocked: Boolean = false,
    val icon: ImageVector,
    val description: String,
    val level: Int
)

enum class ModuleCategory(
    val displayName: String,
    val color: Color,
    val gradient: Brush
) {
    BASICO(
        "Básico",
        Color(0xFF58CC02),
        Brush.verticalGradient(
            colors = listOf(Color(0xFF58CC02), Color(0xFF89E219))
        )
    ),
    INTERMEDIO(
        "Intermedio",
        Color(0xFFFF9600),
        Brush.verticalGradient(
            colors = listOf(Color(0xFFFF9600), Color(0xFFFFB800))
        )
    ),
    AVANZADO(
        "Avanzado",
        Color(0xFF0039A6),
        Brush.verticalGradient(
            colors = listOf(Color(0xFF0039A6), Color(0xFF4A90E2))
        )
    )
}

// ============================================
// MAIN SCREEN
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulesScreen(
    onModuleClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val viewModel: ModulesViewModel = viewModel(factory = ViewModelFactory(context))
    
    // Estados observables del ViewModel
    val backendModules by viewModel.modules.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Convertir ModuleWithProgress a LearningModule para compatibilidad con UI
    val modules = remember(backendModules) {
        backendModules.map { module ->
            LearningModule(
                id = module.id,
                title = module.title,
                category = getCategoryForModule(module.sortOrder),
                lessonsCount = module.totalCount,
                completedCount = module.completedCount,
                isLocked = module.isLocked,
                icon = getIconForModule(module.code),
                description = module.description,
                level = module.sortOrder
            )
        }
    }

    // Encontrar el módulo actual (primer módulo no completado)
    val currentModuleIndex = remember(modules) {
        modules.indexOfFirst { module ->
            !module.isLocked && module.completedCount < module.lessonsCount
        }.takeIf { it >= 0 } ?: 0
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll al módulo actual
    LaunchedEffect(currentModuleIndex) {
        delay(300) // Delay para que la UI se renderice primero
        coroutineScope.launch {
            listState.animateScrollToItem(
                index = currentModuleIndex,
                scrollOffset = -200 // Offset para centrar mejor
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Tu Camino de Aprendizaje",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Nivel ${currentModuleIndex + 1} de ${modules.size}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = Color.White
                ),
                actions = {
                    // Botón de info
                    IconButton(onClick = { /* TODO: Mostrar ayuda */ }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF7F8FA),
                            Color(0xFFE5E7EB)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(modules) { index, module ->
                    val isCurrent = index == currentModuleIndex
                    val isCompleted = module.completedCount >= module.lessonsCount
                    val progress = if (module.lessonsCount > 0)
                        module.completedCount.toFloat() / module.lessonsCount.toFloat()
                    else 0f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Connector Path (excepto para el último)
                        if (index < modules.size - 1) {
                            ProgressPath(
                                isCompleted = isCompleted,
                                progress = progress,
                                category = module.category,
                                modifier = Modifier.height(60.dp)
                            )
                        }

                        // Module Level Node
                        ModuleLevelNode(
                            module = module,
                            isCurrent = isCurrent,
                            isCompleted = isCompleted,
                            progress = progress,
                            onClick = {
                                if (!module.isLocked) {
                                    onModuleClick(module.id)
                                }
                            }
                        )

                        // Connector Path (después del nodo)
                        if (index < modules.size - 1) {
                            ProgressPath(
                                isCompleted = isCompleted,
                                progress = progress,
                                category = module.category,
                                modifier = Modifier.height(60.dp)
                            )
                        }

                        // Espaciado extra
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Trophy final
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    FinalTrophySection()
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

// ============================================
// MODULE LEVEL NODE COMPONENT
// ============================================

@Composable
private fun ModuleLevelNode(
    module: LearningModule,
    isCurrent: Boolean,
    isCompleted: Boolean,
    progress: Float,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isCurrent) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Shake animation for locked modules
    var shakeOffset by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    
    if (module.isLocked) {
        val shake by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shake"
        )
        shakeOffset = shake
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .offset(x = shakeOffset.dp)
            .clickable(
                enabled = !module.isLocked,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(300.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Progress Ring usando componente profesional
                if (!module.isLocked && progress > 0) {
                    AnimatedCircularProgress(
                        progress = progress,
                        modifier = Modifier.size(120.dp),
                        size = 120.dp,
                        strokeWidth = 8.dp,
                        backgroundColor = Color(0xFFE5E7EB),
                        progressColor = module.category.color,
                        showPercentage = false,
                        animationDuration = 1000
                    )
                }

                // Animación de escala para módulos desbloqueados
                val nodeScale by animateFloatAsState(
                    targetValue = if (!module.isLocked) 1f else 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "nodeScale"
                )
                
                // Main Circle
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(nodeScale),
                    shape = CircleShape,
                    color = when {
                        module.isLocked -> Color(0xFFE5E7EB)
                        isCompleted -> module.category.color
                        isCurrent -> Color.White
                        else -> Color.White
                    },
                    shadowElevation = when {
                        isCurrent -> 16.dp
                        !module.isLocked -> 8.dp
                        else -> 2.dp
                    },
                    border = if (isCurrent) BorderStroke(4.dp, module.category.color) else null
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (!module.isLocked && !isCompleted) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            module.category.color.copy(alpha = 0.15f),
                                            module.category.color.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                                }
                            )
                    ) {
                        if (module.isLocked) {
                            // Locked icon con shake animation
                            val shakeOffset by rememberInfiniteTransition(label = "shake").animateFloat(
                                initialValue = -2f,
                                targetValue = 2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(100, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "lockShake"
                            )
                            
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier
                                    .size(40.dp)
                                    .offset(x = shakeOffset.dp)
                            )
                        } else if (isCompleted) {
                            // Animated checkmark para completados con rotation
                            val checkRotation by animateFloatAsState(
                                targetValue = 360f,
                                animationSpec = tween(
                                    durationMillis = 600,
                                    easing = FastOutSlowInEasing
                                ),
                                label = "checkRotation"
                            )
                            
                            Box(modifier = Modifier.rotate(checkRotation)) {
                                AnimatedCheckmark(
                                    isVisible = true,
                                    modifier = Modifier,
                                    color = Color.White,
                                    size = 48.dp
                                )
                            }
                        } else {
                            // Module icon con breathing animation
                            val iconScale by rememberInfiniteTransition(label = "iconBreath").animateFloat(
                                initialValue = 1f,
                                targetValue = if (isCurrent) 1.1f else 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1500, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "iconScale"
                            )
                            
                            Icon(
                                imageVector = module.icon,
                                contentDescription = module.title,
                                tint = module.category.color,
                                modifier = Modifier
                                    .size(48.dp)
                                    .scale(iconScale)
                            )
                        }
                    }
                }

                // Current indicator (pulsing ring)
                if (isCurrent && !module.isLocked) {
                    PulsingRing(color = module.category.color)
                }
                
                // Celebration effect cuando está completado
                if (isCompleted) {
                    CompletionCelebration(
                        show = true,
                        color = module.category.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Module Info Card con gradiente profesional si es actual
            if (isCurrent && !module.isLocked) {
                // Usar GradientCard para módulo actual
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        module.category.color.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Category Badge
                    Surface(
                        color = module.category.color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = module.category.displayName.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = module.category.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title
                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (module.isLocked) Color(0xFF9CA3AF) else AzulTec,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Description
                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Info
                    if (module.isLocked) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Completa el nivel anterior",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${module.completedCount}/${module.lessonsCount} lecciones",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp
                            )
                            
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = module.category.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Usar AnimatedProgressBar profesional
                        AnimatedProgressBar(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color(0xFFE5E7EB),
                            progressColor = module.category.color,
                            height = 8.dp,
                            animationDuration = 1000
                        )

                        if (isCurrent) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Continue button
                            Button(
                                onClick = onClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = module.category.color
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (module.completedCount == 0) "COMENZAR" else "CONTINUAR",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                    }
                }
            } else {
                // Card normal para módulos no actuales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.7f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Category Badge
                        Surface(
                            color = module.category.color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = module.category.displayName.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = module.category.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (module.isLocked) Color(0xFF9CA3AF) else AzulTec,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Description
                        Text(
                            text = module.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Info
                        if (module.isLocked) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Completa el nivel anterior",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${module.completedCount}/${module.lessonsCount} lecciones",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6B7280),
                                    fontSize = 12.sp
                                )
                                
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = module.category.color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Usar AnimatedProgressBar profesional
                            AnimatedProgressBar(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = Color(0xFFE5E7EB),
                                progressColor = module.category.color,
                                height = 8.dp,
                                animationDuration = 1000
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// PROGRESS PATH COMPONENT - Mejorado con efectos
// ============================================

@Composable
private fun ProgressPath(
    isCompleted: Boolean,
    progress: Float,
    category: ModuleCategory,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pathShimmer")
    
    // Efecto de shimmer para paths en progreso
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier.width(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background line con sombra sutil
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(3.dp)
                )
        )

        // Progress line animada con gradiente
        if (progress > 0) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                ),
                label = "pathProgress"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxHeight(animatedProgress)
                    .width(6.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = category.gradient,
                        shape = RoundedCornerShape(3.dp)
                    )
            )
            
            // Shimmer overlay en paths activos (no completados)
            if (!isCompleted && progress < 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(animatedProgress)
                        .width(6.dp)
                        .align(Alignment.TopCenter)
                        .alpha(shimmerAlpha)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
            
            // Partículas flotantes en paths completados
            if (isCompleted) {
                FloatingParticles(
                    color = category.color,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

// ============================================
// FLOATING PARTICLES - Para paths completados
// ============================================

@Composable
private fun FloatingParticles(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    // 3 partículas con diferentes delays
    repeat(3) { index ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + (index * 300),
                    easing = LinearEasing,
                    delayMillis = index * 400
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_$index"
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + (index * 300),
                    easing = LinearEasing,
                    delayMillis = index * 400
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_alpha_$index"
        )
        
        Box(
            modifier = modifier
                .offset(y = offsetY.dp)
                .alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

// ============================================
// PULSING RING ANIMATION - Mejorado
// ============================================

@Composable
private fun PulsingRing(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .alpha(alpha)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

// ============================================
// COMPLETION CELEBRATION - Confetti effect
// ============================================

@Composable
private fun CompletionCelebration(
    show: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (!show) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    
    // Generar partículas de confeti
    repeat(8) { index ->
        val angle = (360f / 8f) * index
        val distance by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 80f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "confetti_distance_$index"
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "confetti_alpha_$index"
        )
        
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 720f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "confetti_rotation_$index"
        )
        
        val radians = Math.toRadians(angle.toDouble())
        val offsetX = (distance * kotlin.math.cos(radians)).toFloat()
        val offsetY = (distance * kotlin.math.sin(radians)).toFloat()
        
        Box(
            modifier = modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .rotate(rotation)
                .alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp, 12.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(color, color.copy(alpha = 0.6f))
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

// ============================================
// FINAL TROPHY SECTION - Mejorado
// ============================================

@Composable
private fun FinalTrophySection() {
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
    
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(3.dp, Color(0xFFFFC800).copy(alpha = glow))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFC800).copy(alpha = 0.15f),
                            Color(0xFFFFD700).copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trofeo con partículas brillantes
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    // Partículas de brillo
                    repeat(6) { index ->
                        val angle = (360f / 6f) * index
                        val distance by infiniteTransition.animateFloat(
                            initialValue = 20f,
                            targetValue = 35f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 2000,
                                    delayMillis = index * 200
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "sparkle_$index"
                        )
                        
                        val sparkleAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 1000,
                                    delayMillis = index * 200
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "sparkle_alpha_$index"
                        )
                        
                        val radians = Math.toRadians(angle.toDouble())
                        val offsetX = (distance * kotlin.math.cos(radians)).toFloat()
                        val offsetY = (distance * kotlin.math.sin(radians)).toFloat()
                        
                        Box(
                            modifier = Modifier
                                .offset(x = offsetX.dp, y = offsetY.dp)
                                .alpha(sparkleAlpha)
                                .size(6.dp)
                                .background(Color(0xFFFFC800), CircleShape)
                        )
                    }
                    
                    // Trofeo con bounce
                    BouncingIcon(
                        modifier = Modifier,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Trophy",
                                tint = Color(0xFFFFC800),
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "¡Meta Final!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFC800)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Completa todos los módulos para dominar LSM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Barra de progreso general animada
                AnimatedProgressBar(
                    progress = 0.75f, // Esto debería ser dinámico según el progreso real
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFE5E7EB),
                    progressColor = Color(0xFFFFC800),
                    height = 10.dp,
                    animationDuration = 1500
                )
            }
        }
    }
}

// ============================================
// HELPER FUNCTIONS
// ============================================

/**
 * Retorna la categoría según el orden del módulo
 */
private fun getCategoryForModule(sortOrder: Int): ModuleCategory {
    return when (sortOrder) {
        in 1..3 -> ModuleCategory.BASICO
        in 4..6 -> ModuleCategory.INTERMEDIO
        else -> ModuleCategory.AVANZADO
    }
}

/**
 * Retorna el ícono según el código del módulo
 */
private fun getIconForModule(code: String): ImageVector {
    return when (code) {
        "abecedario" -> Icons.Default.Star
        "numeros" -> Icons.Default.Add
        "colores" -> Icons.Default.Info
        "animales" -> Icons.Default.Face
        "comida" -> Icons.Default.Favorite
        "familia" -> Icons.Default.Face
        "hogar" -> Icons.Default.Home
        "frutas" -> Icons.Default.Favorite
        else -> Icons.Default.Star
    }
}
