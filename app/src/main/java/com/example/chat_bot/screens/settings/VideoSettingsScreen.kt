package com.example.chat_bot.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chat_bot.data.preferences.UserPreferences
import com.example.chat_bot.ui.theme.AzulTec
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    
    // Estado de velocidad de video
    val videoSpeed by userPreferences.videoSpeed.collectAsState(initial = 1.0f)
    
    val speedOptions = listOf(
        0.5f to "0.5x (Muy lento)",
        0.75f to "0.75x (Lento)",
        1.0f to "1.0x (Normal)",
        1.25f to "1.25x (Rápido)",
        1.5f to "1.5x (Más rápido)",
        2.0f to "2.0x (Muy rápido)"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Velocidad de Videos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Selecciona la velocidad predeterminada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Esta configuración se aplicará a todos los videos LSM en la app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            speedOptions.forEach { (speed, label) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = {
                        scope.launch {
                            userPreferences.setVideoSpeed(speed)
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (videoSpeed == speed) 
                            AzulTec.copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colorScheme.surface
                    ),
                    border = if (videoSpeed == speed) 
                        CardDefaults.outlinedCardBorder().copy(width = 2.dp)
                    else 
                        CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (videoSpeed == speed) FontWeight.Bold else FontWeight.Normal
                        )
                        
                        if (videoSpeed == speed) {
                            RadioButton(
                                selected = true,
                                onClick = null
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Consejo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Si estás comenzando, te recomendamos usar velocidad 0.75x para aprender mejor los movimientos. Puedes aumentarla cuando te sientas más cómodo.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
