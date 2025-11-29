package com.example.chat_bot.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_bot.R
import com.example.chat_bot.data.auth.AuthState
import com.example.chat_bot.data.models.UserLevel
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import com.example.chat_bot.viewmodels.ProfileViewModel
import com.example.chat_bot.viewmodels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: com.example.chat_bot.viewmodels.AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToVideoSettings: () -> Unit = {},
    onNavigateToAppearance: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    userLevel: UserLevel? = null,
    unlockedAchievements: Int = 12,
    totalAchievements: Int = 25
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    // Estados observables
    val authState by authViewModel.authState.collectAsState()

    val userName by profileViewModel.userName.collectAsState()
    val userEmail by profileViewModel.userEmail.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    
    // Estadísticas del ViewModel
    val modulesCompleted by profileViewModel.modulesCompleted.collectAsState()
    val totalProgress by profileViewModel.totalProgress.collectAsState()
    val currentStreak by profileViewModel.currentStreak.collectAsState()
    val signsLearned by profileViewModel.signsLearned.collectAsState()
    
    // Calcular XP y lecciones basadas en estadísticas
    val totalXP = signsLearned * 10 // Aproximación: 10 XP por seña
    val completedLessons = modulesCompleted * 10 // Aproximación: 10 lecciones por módulo
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Observar cambio de estado de autenticación
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            onLogout()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mi Perfil",
                        fontWeight = FontWeight.Bold
                    )
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
                .verticalScroll(rememberScrollState())
        ) {
            // Header con información del usuario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulTec)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.borrego_normal),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    if (userLevel != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Circular progress para nivel
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AnimatedCircularProgress(
                                progress = userLevel.progress,
                                modifier = Modifier,
                                size = 60.dp,
                                strokeWidth = 6.dp,
                                backgroundColor = Color.White.copy(alpha = 0.2f),
                                progressColor = AmarilloOro,
                                showPercentage = false,
                                animationDuration = 1000
                            )
                            
                            Column {
                                Text(
                                    text = "Nivel ${userLevel.level}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = UserLevel.getLevelTitle(userLevel.level),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "${userLevel.currentXP}/${userLevel.requiredXP} XP",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmarilloOro
                                )
                            }
                        }
                    }
                }
            }
        
            Spacer(modifier = Modifier.height(24.dp))
            
            // Statistics Cards
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        icon = Icons.Default.EmojiEvents,
                        value = "$totalXP",
                        label = "XP Total",
                        color = AmarilloOro,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        icon = Icons.Default.LocalFireDepartment,
                        value = "$currentStreak",
                        label = "Racha",
                        color = RojoError,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        icon = Icons.Default.School,
                        value = "$modulesCompleted",
                        label = "Módulos",
                        color = AzulInfo,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        icon = Icons.Default.CheckCircle,
                        value = "$completedLessons",
                        label = "Lecciones",
                        color = VerdeExito,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Achievements summary card con animación
                Card(
                    onClick = onNavigateToAchievements,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BouncingIcon(
                                    modifier = Modifier,
                                    content = {
                                        Icon(
                                            Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = AmarilloOro,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                )
                                Column {
                                    Text(
                                        text = "Logros Desbloqueados",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = GrisMedio
                                    )
                                    Text(
                                        text = "$unlockedAchievements / $totalAchievements",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulTec
                                    )
                                }
                            }
                            
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = GrisOscuro.copy(alpha = 0.4f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Barra de progreso de logros
                        AnimatedProgressBar(
                            progress = unlockedAchievements.toFloat() / totalAchievements.toFloat(),
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = GrisClaro,
                            progressColor = AmarilloOro,
                            height = 8.dp,
                            animationDuration = 1000
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Opciones
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Configuración",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AzulTec,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                ProfileOption(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    subtitle = "Recordatorios y alertas",
                    onClick = onNavigateToNotifications
                )
                
                ProfileOption(
                    icon = Icons.Default.Speed,
                    title = "Velocidad de Videos",
                    subtitle = "Ajusta la reproducción",
                    onClick = onNavigateToVideoSettings
                )
                
                ProfileOption(
                    icon = Icons.Default.Palette,
                    title = "Apariencia",
                    subtitle = "Modo oscuro y temas",
                    onClick = onNavigateToAppearance
                )
                
                ProfileOption(
                    icon = Icons.Default.Security,
                    title = "Privacidad y Datos",
                    subtitle = "Control de información",
                    onClick = onNavigateToPrivacy
                )
                
                ProfileOption(
                    icon = Icons.Default.Info,
                    title = "Acerca de EnSeñas",
                    subtitle = "Versión 1.0-beta",
                    onClick = { /* Mostrar diálogo About */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ProfileOption(
                    icon = Icons.Default.ExitToApp,
                    title = "Cerrar Sesión",
                    iconTint = RojoError,
                    onClick = {
                        showLogoutDialog = true
                        authViewModel.signOut()
                        onLogout()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = RojoError
                )
            },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro de que deseas cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoError
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Cerrar Sesión")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Loading overlay durante carga de stats y logout
    SimpleLoadingOverlay(isLoading = isLoading)
    }
}

@Composable
private fun StatisticCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulTec
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = GrisOscuro.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = AzulTec,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AzulTec
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisOscuro.copy(alpha = 0.6f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GrisOscuro.copy(alpha = 0.4f)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
