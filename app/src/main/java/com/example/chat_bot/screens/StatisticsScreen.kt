package com.example.chat_bot.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.ui.theme.*

data class ModuleStatistic(
    val name: String,
    val timeSpent: Int, // en minutos
    val progress: Float,
    val icon: ImageVector,
    val color: Color
)

data class ActivityStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Datos mock de estadísticas
    val totalXP = 2450
    val totalScreenTime = 285 // minutos
    val activitiesCompleted = 47
    val currentStreak = 7
    val bestStreak = 12
    val averageSessionTime = 18 // minutos
    
    val moduleStats = listOf(
        ModuleStatistic("Abecedario", 85, 0.75f, Icons.Default.Abc, AzulTec),
        ModuleStatistic("Números", 62, 0.60f, Icons.Default.Numbers, VerdeExito),
        ModuleStatistic("Colores", 48, 0.45f, Icons.Default.Palette, NaranjaEnergia),
        ModuleStatistic("Animales", 35, 0.30f, Icons.Default.Pets, AmarilloOro),
        ModuleStatistic("Saludos", 55, 0.50f, Icons.Default.WavingHand, Color(0xFFF8BBD0))
    )
    
    val activityStats = listOf(
        ActivityStat("Lecciones completadas", "32", Icons.Default.School, AzulTec),
        ActivityStat("Quizzes realizados", "28", Icons.Default.Quiz, VerdeExito),
        ActivityStat("Juegos de memoria", "15", Icons.Default.SportsEsports, NaranjaEnergia),
        ActivityStat("Palabras aprendidas", "126", Icons.Default.Translate, AmarilloOro)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Estadísticas Avanzadas",
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
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AzulTec.copy(alpha = 0.05f),
                            FondoClaro
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Resumen General
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resumen General",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AzulTec
                            )
                            Icon(
                                Icons.Default.Analytics,
                                contentDescription = null,
                                tint = AzulTec,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                value = "$totalXP XP",
                                label = "Total",
                                icon = Icons.Default.Star,
                                color = AmarilloOro
                            )
                            StatCard(
                                value = "${totalScreenTime / 60}h ${totalScreenTime % 60}m",
                                label = "Tiempo Total",
                                icon = Icons.Default.AccessTime,
                                color = AzulInfo
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(
                                value = "$currentStreak días",
                                label = "Racha Actual",
                                icon = Icons.Default.LocalFireDepartment,
                                color = NaranjaEnergia
                            )
                            StatCard(
                                value = "$bestStreak días",
                                label = "Mejor Racha",
                                icon = Icons.Default.MilitaryTech,
                                color = VerdeExito
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tiempo por módulo
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiempo por Módulo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AzulTec
                            )
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = AzulTec
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        moduleStats.forEach { stat ->
                            ModuleTimeCard(stat)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Actividades realizadas
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Actividades Diarias",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AzulTec
                            )
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VerdeExito
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        activityStats.forEach { stat ->
                            ActivityStatRow(stat)
                            if (stat != activityStats.last()) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = GrisClaro
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Estadísticas adicionales
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Rendimiento",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulTec
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PerformanceRow(
                            label = "Sesión promedio",
                            value = "$averageSessionTime min",
                            icon = Icons.Default.Speed
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = GrisClaro
                        )
                        PerformanceRow(
                            label = "Precisión en quizzes",
                            value = "87%",
                            icon = Icons.Default.TrackChanges
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = GrisClaro
                        )
                        PerformanceRow(
                            label = "Juegos completados",
                            value = "15/20",
                            icon = Icons.Default.SportsScore
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(140.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AzulTec,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GrisOscuro.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ModuleTimeCard(stat: ModuleStatistic) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(stat.color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = null,
                tint = stat.color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stat.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulTec
                )
                Text(
                    text = "${stat.timeSpent} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisOscuro,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = stat.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = stat.color,
                trackColor = stat.color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun ActivityStatRow(stat: ActivityStat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(stat.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisOscuro
            )
        }
        Text(
            text = stat.value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = stat.color
        )
    }
}

@Composable
fun PerformanceRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AzulTec,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisOscuro
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = AzulTec
        )
    }
}
