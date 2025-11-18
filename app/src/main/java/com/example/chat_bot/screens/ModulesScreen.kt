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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.ui.theme.*
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
    val modules = remember {
        listOf(
            LearningModule(
                id = 1,
                title = "Abecedario",
                category = ModuleCategory.BASICO,
                lessonsCount = 26,
                completedCount = 20,
                isLocked = false,
                icon = Icons.Default.Star,
                description = "Aprende las 26 letras de LSM",
                level = 1
            ),
            LearningModule(
                id = 2,
                title = "Números",
                category = ModuleCategory.BASICO,
                lessonsCount = 20,
                completedCount = 9,
                isLocked = false,
                icon = Icons.Default.Add,
                description = "Cuenta del 0 al 100 en señas",
                level = 2
            ),
            LearningModule(
                id = 3,
                title = "Colores",
                category = ModuleCategory.BASICO,
                lessonsCount = 13,
                completedCount = 0,
                isLocked = false,
                icon = Icons.Default.Info,
                description = "Colores básicos y sus variantes",
                level = 3
            ),
            LearningModule(
                id = 4,
                title = "Animales",
                category = ModuleCategory.INTERMEDIO,
                lessonsCount = 27,
                completedCount = 0,
                isLocked = false,
                icon = Icons.Default.Face,
                description = "Animales domésticos y salvajes",
                level = 4
            ),
            LearningModule(
                id = 5,
                title = "Comida",
                category = ModuleCategory.INTERMEDIO,
                lessonsCount = 30,
                completedCount = 0,
                isLocked = true,
                icon = Icons.Default.Favorite,
                description = "Alimentos y bebidas comunes",
                level = 5
            ),
            LearningModule(
                id = 6,
                title = "Familia",
                category = ModuleCategory.INTERMEDIO,
                lessonsCount = 15,
                completedCount = 0,
                isLocked = true,
                icon = Icons.Default.Face,
                description = "Miembros de la familia",
                level = 6
            ),
            LearningModule(
                id = 7,
                title = "Hogar",
                category = ModuleCategory.AVANZADO,
                lessonsCount = 25,
                completedCount = 0,
                isLocked = true,
                icon = Icons.Default.Home,
                description = "Objetos y espacios del hogar",
                level = 7
            ),
            LearningModule(
                id = 8,
                title = "Frutas",
                category = ModuleCategory.BASICO,
                lessonsCount = 22,
                completedCount = 0,
                isLocked = true,
                icon = Icons.Default.Favorite,
                description = "Frutas tropicales y comunes",
                level = 8
            )
        )
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
                // Progress Ring
                if (!module.isLocked && progress > 0) {
                    CircularProgressRing(
                        progress = progress,
                        color = module.category.color,
                        modifier = Modifier.size(120.dp)
                    )
                }

                // Main Circle
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = when {
                        module.isLocked -> Color(0xFFE5E7EB)
                        isCompleted -> module.category.color
                        isCurrent -> Color.White
                        else -> Color.White
                    },
                    shadowElevation = if (isCurrent) 12.dp else 6.dp,
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
                                            module.category.color.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                                }
                            )
                    ) {
                        if (module.isLocked) {
                            // Locked icon
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(40.dp)
                            )
                        } else if (isCompleted) {
                            // Check icon
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Completado",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            // Module icon
                            Icon(
                                imageVector = module.icon,
                                contentDescription = module.title,
                                tint = module.category.color,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                // Current indicator (pulsing ring)
                if (isCurrent && !module.isLocked) {
                    PulsingRing(color = module.category.color)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Module Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color.White else Color.White.copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isCurrent) 8.dp else 2.dp
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

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = module.category.color,
                            trackColor = Color(0xFFE5E7EB)
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
    }
}

// ============================================
// PROGRESS PATH COMPONENT
// ============================================

@Composable
private fun ProgressPath(
    isCompleted: Boolean,
    progress: Float,
    category: ModuleCategory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background line
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(2.dp)
                )
        )

        // Progress line
        if (progress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(progress)
                    .width(4.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = category.gradient,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

// ============================================
// CIRCULAR PROGRESS RING
// ============================================

@Composable
private fun CircularProgressRing(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "progress"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Background circle
        drawCircle(
            color = Color(0xFFE5E7EB),
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )

        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(
                centerX - radius,
                centerY - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

// ============================================
// PULSING RING ANIMATION
// ============================================

@Composable
private fun PulsingRing(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
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
                color = color.copy(alpha = 0.3f),
                shape = CircleShape
            )
    )
}

// ============================================
// FINAL TROPHY SECTION
// ============================================

@Composable
private fun FinalTrophySection() {
    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFC800).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, Color(0xFFFFC800))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Trophy",
                tint = Color(0xFFFFC800),
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotation)
            )
            
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
        }
    }
}
