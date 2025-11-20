package com.example.chat_bot.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.R
import com.example.chat_bot.data.models.LeaderboardEntry
import com.example.chat_bot.data.models.LeaderboardResponse
import com.example.chat_bot.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    leaderboardData: LeaderboardResponse? = null,
    currentUserId: String = "3",
    onNavigateBack: () -> Unit,
    onTypeChange: (String) -> Unit = {}
) {
    var selectedType by remember { mutableStateOf("weekly") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Clasificación",
                        fontWeight = FontWeight.Bold
                    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Type selector tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulTec.copy(alpha = 0.05f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LeaderboardTab(
                    title = "Semanal",
                    icon = Icons.Default.CalendarMonth,
                    isSelected = selectedType == "weekly",
                    onClick = { 
                        selectedType = "weekly"
                        onTypeChange("weekly")
                    },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardTab(
                    title = "Todo el Tiempo",
                    icon = Icons.Default.EmojiEvents,
                    isSelected = selectedType == "all_time",
                    onClick = { 
                        selectedType = "all_time"
                        onTypeChange("all_time")
                    },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardTab(
                    title = "Amigos",
                    icon = Icons.Default.People,
                    isSelected = selectedType == "friends",
                    onClick = { 
                        selectedType = "friends"
                        onTypeChange("friends")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (leaderboardData != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    // Top 3 podium
                    item {
                        if (leaderboardData.entries.isNotEmpty()) {
                            PodiumSection(
                                entries = leaderboardData.entries.take(3),
                                leaderboardType = selectedType
                            )
                        }
                    }
                    
                    // Rest of leaderboard
                    if (leaderboardData.entries.size > 3) {
                        itemsIndexed(leaderboardData.entries.drop(3)) { index, entry ->
                            LeaderboardEntryCard(
                                entry = entry,
                                rank = index + 4,
                                isCurrentUser = entry.userId == currentUserId,
                                leaderboardType = selectedType,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // User's rank at bottom
                if (leaderboardData.userRank > 3) {
                    CurrentUserRankCard(
                        rank = leaderboardData.userRank,
                        totalUsers = leaderboardData.totalUsers,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AzulTec)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTab(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) AzulTec else Color.Transparent
    val contentColor = if (isSelected) Color.White else GrisOscuro
    
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PodiumSection(
    entries: List<LeaderboardEntry>,
    leaderboardType: String
) {
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏆 Top 3",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AzulTec
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Arrange podium: 2nd, 1st, 3rd
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // 2nd place
            if (entries.size > 1) {
                PodiumCard(
                    entry = entries[1],
                    rank = 2,
                    height = 140.dp,
                    medal = "🥈",
                    leaderboardType = leaderboardType
                )
            }
            
            // 1st place
            if (entries.isNotEmpty()) {
                PodiumCard(
                    entry = entries[0],
                    rank = 1,
                    height = 180.dp,
                    medal = "🥇",
                    leaderboardType = leaderboardType
                )
            }
            
            // 3rd place
            if (entries.size > 2) {
                PodiumCard(
                    entry = entries[2],
                    rank = 3,
                    height = 120.dp,
                    medal = "🥉",
                    leaderboardType = leaderboardType
                )
            }
        }
    }
}

@Composable
private fun PodiumCard(
    entry: LeaderboardEntry,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    medal: String,
    leaderboardType: String
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val rotation by rememberInfiniteTransition(label = "medal_rotation").animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "medal_rotation"
    )
    
    Column(
        modifier = Modifier
            .width(100.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Medal
        Text(
            text = medal,
            fontSize = 32.sp,
            modifier = Modifier.graphicsLayer {
                rotationZ = if (rank == 1) rotation else 0f
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Avatar
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(AzulTec.copy(alpha = 0.1f))
                .border(
                    width = 3.dp,
                    color = when (rank) {
                        1 -> AmarilloOro
                        2 -> Color(0xFFC0C0C0) // Silver
                        else -> Color(0xFFCD7F32) // Bronze
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.borrego_normal),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Username
        Text(
            text = entry.username,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = GrisOscuro,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // XP
        Text(
            text = if (leaderboardType == "weekly") {
                "${entry.weeklyXP ?: entry.totalXP} XP"
            } else {
                "${entry.totalXP} XP"
            },
            style = MaterialTheme.typography.labelSmall,
            color = GrisMedio,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Podium base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            when (rank) {
                                1 -> AmarilloOro.copy(alpha = 0.8f)
                                2 -> Color(0xFFC0C0C0).copy(alpha = 0.8f)
                                else -> Color(0xFFCD7F32).copy(alpha = 0.8f)
                            },
                            when (rank) {
                                1 -> AmarilloOro.copy(alpha = 0.6f)
                                2 -> Color(0xFFC0C0C0).copy(alpha = 0.6f)
                                else -> Color(0xFFCD7F32).copy(alpha = 0.6f)
                            }
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun LeaderboardEntryCard(
    entry: LeaderboardEntry,
    rank: Int,
    isCurrentUser: Boolean,
    leaderboardType: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isCurrentUser) AzulTec.copy(alpha = 0.1f) else Color.White
    val borderColor = if (isCurrentUser) AzulTec else Color.Transparent
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GrisClaro),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = GrisOscuro
                )
            }
            
            // Avatar
            Image(
                painter = painterResource(id = R.drawable.borrego_normal),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AzulTec.copy(alpha = 0.1f))
            )
            
            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrentUser) AzulTec else GrisOscuro,
                        maxLines = 1
                    )
                    if (isCurrentUser) {
                        Text(
                            text = "(Tú)",
                            style = MaterialTheme.typography.labelSmall,
                            color = AzulTec
                        )
                    }
                    if (entry.isFriend) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Amigo",
                            tint = VerdeExito,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Nivel ${entry.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GrisMedio
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = GrisMedio
                    )
                    Text(
                        text = if (leaderboardType == "weekly") {
                            "${entry.weeklyXP ?: entry.totalXP} XP esta semana"
                        } else {
                            "${entry.totalXP} XP total"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = GrisMedio
                    )
                }
            }
            
            // XP badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AmarilloOro.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                        text = if (leaderboardType == "weekly") {
                            "${entry.weeklyXP ?: entry.totalXP}"
                        } else {
                            "${entry.totalXP}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmarilloOro
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentUserRankCard(
    rank: Int,
    totalUsers: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AzulTec,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tu Posición",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "#$rank de $totalUsers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = VerdeExito,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
