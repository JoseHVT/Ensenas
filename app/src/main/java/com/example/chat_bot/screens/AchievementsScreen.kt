package com.example.chat_bot.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.data.models.Achievement
import com.example.chat_bot.data.models.AchievementCategory
import com.example.chat_bot.data.models.Achievements
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    achievements: List<Achievement> = Achievements.ALL,
    onNavigateBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }
    
    val filteredAchievements = if (selectedCategory != null) {
        achievements.filter { it.category == selectedCategory }
    } else {
        achievements
    }
    
    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size
    val completionPercentage = (unlockedCount.toFloat() / totalCount.toFloat() * 100).toInt()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Logros",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$unlockedCount/$totalCount desbloqueados",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress Overview
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    AzulTec.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Progreso Global",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulTec
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Progress bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$completionPercentage%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = VerdeExito
                            )
                            Text(
                                text = "$unlockedCount de $totalCount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisMedio
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        AnimatedProgressBar(
                            progress = unlockedCount.toFloat() / totalCount.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp),
                            backgroundColor = GrisClaro,
                            progressColor = VerdeExito,
                            animationDuration = 1500
                        )
                    }
                }
            }
            
            // Category filters
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GrisOscuro
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            label = "Todas",
                            count = totalCount,
                            isSelected = selectedCategory == null,
                            onClick = { selectedCategory = null }
                        )
                        
                        AchievementCategory.entries.forEach { category ->
                            val count = achievements.count { it.category == category }
                            CategoryChip(
                                label = getCategoryLabel(category),
                                count = count,
                                isSelected = selectedCategory == category,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }
            
            // Achievements list
            items(filteredAchievements) { achievement ->
                AchievementCard(
                    achievement = achievement,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) AzulTec else GrisClaro
    val textColor = if (isSelected) Color.White else GrisOscuro
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            Text(
                text = "($count)",
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (achievement.isUnlocked) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) Color.White else GrisClaro.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (achievement.isUnlocked) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon con animación
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) {
                            getCategoryColor(achievement.category).copy(alpha = 0.15f)
                        } else {
                            GrisClaro
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (achievement.isUnlocked) {
                    BouncingIcon(
                        modifier = Modifier.size(60.dp),
                        content = {
                            Text(
                                text = achievement.icon,
                                fontSize = 32.sp
                            )
                        }
                    )
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = GrisMedio,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) AzulTec else GrisOscuro
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisMedio
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar (si no está desbloqueado)
                if (!achievement.isUnlocked) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${achievement.progress}/${achievement.requirement}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GrisMedio
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        LinearProgressIndicator(
                            progress = { achievement.progress.toFloat() / achievement.requirement.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = getCategoryColor(achievement.category),
                            trackColor = GrisClaro
                        )
                    }
                } else {
                    // XP reward
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = AmarilloOro,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+${achievement.xpReward} XP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmarilloOro
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        if (achievement.unlockedAt != null) {
                            Text(
                                text = "Desbloqueado",
                                style = MaterialTheme.typography.labelSmall,
                                color = VerdeExito
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getCategoryLabel(category: AchievementCategory): String = when (category) {
    AchievementCategory.LECCIONES -> "Lecciones"
    AchievementCategory.RACHA -> "Racha"
    AchievementCategory.PRECISION -> "Precisión"
    AchievementCategory.VELOCIDAD -> "Velocidad"
    AchievementCategory.MAESTRIA -> "Maestría"
    AchievementCategory.SOCIAL -> "Social"
    AchievementCategory.ESPECIAL -> "Especial"
}

@Composable
private fun getCategoryColor(category: AchievementCategory): Color = when (category) {
    AchievementCategory.LECCIONES -> VerdeExito
    AchievementCategory.RACHA -> RojoError
    AchievementCategory.PRECISION -> AzulInfo
    AchievementCategory.VELOCIDAD -> AmarilloOro
    AchievementCategory.MAESTRIA -> AzulTec
    AchievementCategory.SOCIAL -> NaranjaEnergia
    AchievementCategory.ESPECIAL -> Color(0xFFAA00FF)
}

/**
 * Achievement Notification Popup
 */
@Composable
fun AchievementNotificationPopup(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val rotation by rememberInfiniteTransition(label = "trophy_rotation").animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_rotation"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .scale(scale),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy icon
                Text(
                    text = "🎉",
                    fontSize = 48.sp,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "¡Logro Desbloqueado!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AzulTec
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Achievement icon
                Text(
                    text = achievement.icon,
                    fontSize = 64.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisOscuro,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisMedio,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // XP reward
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AmarilloOro.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AmarilloOro,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+${achievement.xpReward} XP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmarilloOro
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulTec
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "¡Genial!",
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
