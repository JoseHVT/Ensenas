package com.example.chat_bot.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_bot.R
import com.example.chat_bot.data.models.DailyGoal
import com.example.chat_bot.data.models.UserLevel
import com.example.chat_bot.ui.components.LoadingOverlay
import com.example.chat_bot.ui.components.SimpleLoadingOverlay
import com.example.chat_bot.ui.components.pressAnimation
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import com.example.chat_bot.viewmodels.HomeViewModel
import com.example.chat_bot.viewmodels.ViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToModules: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToChatBot: () -> Unit = {},
    onNavigateToMemoryGame: (moduleId: Int) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = ViewModelFactory(context))
    val haptic = rememberHapticFeedback()
    
    // Estados observables del ViewModel
    val userLevel by viewModel.userLevel.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AzulTec.copy(alpha = 0.05f),
                            FondoClaro
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
        // Header compacto con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AzulTec, AzulTec.copy(alpha = 0.9f))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola${if (userName.isNotEmpty()) ", " + userName.split(" ").firstOrNull() else ""}! 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentStreak > 0) {
                            "Sigues una racha de $currentStreak día${if (currentStreak > 1) "s" else ""}"
                        } else {
                            "¡Comienza tu racha hoy!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    if (userLevel != null) {
                        val level = userLevel!! // Safe to use after null check
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nivel ${level.level} • ${UserLevel.getLevelTitle(level.level)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
        
        // Racha semanal con calendario
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                StreakCalendar(currentStreak = currentStreak)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // XP Progress Bar
        if (dailyGoal != null) {
            val goal = dailyGoal!! // Safe after null check
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(),
                label = "XP Progress"
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    XPProgressCard(
                        currentXP = goal.currentXP,
                        dailyGoal = goal.targetXP
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // User Level Progress (si está disponible)
        if (userLevel != null) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    UserLevelCard(userLevel = userLevel!!)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        // XP Progress Bar
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(),
            label = "XP Progress"
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                XPProgressCard(currentXP = 245, dailyGoal = 50)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Lección diaria sugerida
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                DailyLessonCard(
                    moduleName = "Abecedario",
                    progress = 0.75f,
                    onClick = onNavigateToModules
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Desafíos semanales
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                WeeklyChallengesCard()
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Accesos rápidos mejorados
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "Explora",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AzulTec
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniQuickAccessCard(
                    title = "Diccionario",
                    icon = Icons.Default.MenuBook,
                    color = AzulInfo,
                    onClick = onNavigateToDictionary,
                    modifier = Modifier.weight(1f)
                )
                MiniQuickAccessCard(
                    title = "Práctica",
                    icon = Icons.Default.Quiz,
                    color = VerdeExito,
                    onClick = onNavigateToModules,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniQuickAccessCard(
                    title = "Logros",
                    icon = Icons.Default.EmojiEvents,
                    color = AmarilloOro,
                    onClick = onNavigateToAchievements,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniQuickAccessCard(
                    title = "Clasificación",
                    icon = Icons.Default.Leaderboard,
                    color = NaranjaEnergia,
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
                MiniQuickAccessCard(
                    title = "Estadísticas",
                    icon = Icons.Default.Analytics,
                    color = AzulInfo,
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniQuickAccessCard(
                    title = "Juegos",
                    icon = Icons.Default.SportsEsports,
                    color = RojoError.copy(alpha = 0.8f),
                    onClick = { onNavigateToMemoryGame(0) }, // moduleId 0 para modo libre
                    modifier = Modifier.weight(1f)
                )
                MiniQuickAccessCard(
                    title = "Logros",
                    icon = Icons.Default.EmojiEvents,
                    color = AmarilloAdvertencia,
                    onClick = onNavigateToAchievements,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    // Floating Action Button para el ChatBot
    FloatingActionButton(
        onClick = onNavigateToChatBot,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp)
            .pressAnimation(),
        containerColor = AzulTec,
        contentColor = Blanco,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🐏",
                fontSize = 24.sp
            )
            Text(
                text = "BorregoBot",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
    
    // Loading overlay durante carga de estadísticas
    SimpleLoadingOverlay(isLoading = isLoading)
  }
}

@Composable
private fun AnimatedBorregoIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "borrego")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    
    Image(
        painter = painterResource(id = R.drawable.borrego_celebration),
        contentDescription = "Borrego",
        modifier = Modifier
            .size(70.dp)
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            }
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .padding(8.dp)
    )
}

// ============================================
// FLAME ICON WITH PULSING ANIMATION
// ============================================

@Composable
private fun FlameIcon(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameAlpha"
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(RojoError.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            // Glow effect
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                RojoError.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Racha",
            tint = if (isActive) RojoError.copy(alpha = alpha) else GrisMedio,
            modifier = Modifier
                .size(28.dp)
                .scale(if (isActive) scale else 1f)
        )
        
        if (isActive) {
            // Pulsing dot indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                PulsingDot(
                    color = VerdeExito,
                    size = 10.dp
                )
            }
        }
    }
}

@Composable
private fun DayCircle(day: String, isCompleted: Boolean, isCurrent: Boolean) {
    val backgroundColor = when {
        isCurrent -> VerdeExito
        isCompleted -> VerdeExito.copy(alpha = 0.3f)
        else -> GrisClaro
    }
    
    val textColor = when {
        isCurrent -> Color.White
        isCompleted -> VerdeExito
        else -> GrisOscuro.copy(alpha = 0.5f)
    }
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(backgroundColor, CircleShape)
            .border(
                width = if (isCurrent) 2.dp else 0.dp,
                color = VerdeExito,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun StreakCalendar(currentStreak: Int) {
    // Animated counter con odometer effect
    var displayedStreak by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(currentStreak) {
        val startValue = displayedStreak
        val endValue = currentStreak
        val duration = 800L
        val startTime = System.currentTimeMillis()
        
        while (displayedStreak < endValue) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            displayedStreak = (startValue + (endValue - startValue) * progress).toInt()
            delay(16) // ~60 FPS
        }
        displayedStreak = endValue
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Animated fire icon con pulsación
                FlameIcon(isActive = currentStreak > 0)
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Animated counter
                Row(verticalAlignment = Alignment.Bottom) {
                    AnimatedCounter(
                        targetValue = displayedStreak,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = RojoError,
                        durationMs = 800
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "día${if (displayedStreak != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrisMedio,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val days = listOf("L", "M", "X", "J", "V", "S", "D")
                // Simular días completados basado en racha actual
                val completedDays = days.mapIndexed { index, _ -> index < currentStreak.coerceAtMost(7) }
                
                days.forEachIndexed { index, day ->
                    DayCircle(
                        day = day,
                        isCompleted = completedDays[index],
                        isCurrent = index == (currentStreak - 1).coerceIn(0, 6)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserLevelCard(userLevel: UserLevel) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(userLevel.progress) {
        animatedProgress = 0f
        delay(300)
        animatedProgress = userLevel.progress
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = UserLevel.getLevelTitle(userLevel.level),
                        style = MaterialTheme.typography.labelMedium,
                        color = GrisMedio
                    )
                    Text(
                        text = "Nivel ${userLevel.level}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AzulTec
                    )
                }
                
                // Usar AnimatedCircularProgress profesional
                AnimatedCircularProgress(
                    progress = animatedProgress,
                    modifier = Modifier,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    backgroundColor = GrisClaro.copy(alpha = 0.3f),
                    progressColor = AzulTec,
                    showPercentage = false,
                    animationDuration = 1000
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${userLevel.currentXP} / ${userLevel.requiredXP} XP",
                style = MaterialTheme.typography.bodySmall,
                color = GrisMedio
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Usar AnimatedProgressBar profesional
            AnimatedProgressBar(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = GrisClaro,
                progressColor = AzulTec,
                height = 12.dp,
                animationDuration = 1000
            )
        }
    }
}

@Composable
private fun XPProgressCard(currentXP: Int, dailyGoal: Int) {
    val progress = (currentXP % dailyGoal).toFloat() / dailyGoal
    var animatedProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress = 0f
        delay(300)
        animatedProgress = progress
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Meta Diaria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AzulTec
                    )
                    Text(
                        text = "${currentXP % dailyGoal} / $dailyGoal XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisOscuro.copy(alpha = 0.7f)
                    )
                }
                
                Surface(
                    color = AmarilloAdvertencia.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(60.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = AmarilloAdvertencia,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "$currentXP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmarilloAdvertencia,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Usar componente profesional AnimatedProgressBar
            AnimatedProgressBar(
                progress = animatedProgress,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = GrisClaro,
                progressColor = VerdeExito,
                height = 12.dp,
                animationDuration = 800
            )
        }
    }
}

@Composable
private fun DailyLessonCard(
    moduleName: String,
    progress: Float,
    onClick: () -> Unit
) {
    PressableCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent,
        elevation = 8.dp,
        cornerRadius = 20.dp,
        pressScale = 0.98f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            AzulTec,
                            AzulTec.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con animación sutil
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .then(
                        Modifier.graphicsLayer {
                            // Pequeña rotación para dinamismo
                            rotationZ = 5f
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "📚 Lección diaria",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = moduleName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Usar AnimatedProgressBar para animación fluida
                AnimatedProgressBar(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    gradient = Brush.horizontalGradient(
                        colors = listOf(VerdeExito, VerdeExitoLight)
                    ),
                    trackColor = Color.White.copy(alpha = 0.3f),
                    animationDurationMs = 800
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun ChallengeItem(
    text: String,
    completed: Boolean,
    progress: Int,
    total: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (completed) VerdeExito else GrisClaro,
                    CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (completed) VerdeExito else GrisOscuro.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (completed) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (completed) GrisOscuro.copy(alpha = 0.6f) else AzulTec,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$progress/$total",
                style = MaterialTheme.typography.bodySmall,
                color = GrisOscuro.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun WeeklyChallengesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono con animación de rebote
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AmarilloAdvertencia.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AmarilloAdvertencia,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                rotationZ = -10f
                            }
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "🎯 Desafíos Semanales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulTec
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Usar StaggeredAnimation para entrada progresiva
            ChallengeItem(
                text = "Completa 5 lecciones",
                completed = true,
                progress = 5,
                total = 5
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChallengeItem(
                text = "Practica 3 días seguidos",
                completed = false,
                progress = 2,
                total = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChallengeItem(
                text = "Gana 200 XP",
                completed = false,
                progress = 145,
                total = 200
            )
        }
    }
}

@Composable
private fun MiniQuickAccessCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PressableCard(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        backgroundColor = color.copy(alpha = 0.1f),
        elevation = 2.dp,
        cornerRadius = 16.dp,
        pressScale = 0.95f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación de rebote para iconos especiales
            if (title == "Logros" || icon == Icons.Default.EmojiEvents) {
                BouncingIcon(
                    modifier = Modifier,
                    content = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            // Sutil rotación para dinamismo
                            rotationZ = -5f
                        }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}
