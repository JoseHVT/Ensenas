package com.example.chat_bot.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import com.example.chat_bot.data.api.RetrofitInstance
import kotlinx.coroutines.launch

data class Sign(
    val id: Int,
    val word: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    onSignClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var signs by remember { mutableStateOf<List<Sign>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Cargar señas del backend
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getSigns(limit = 100)
                if (response.isSuccessful && response.body() != null) {
                    signs = response.body()!!.map { signResponse ->
                        Sign(
                            id = signResponse.id,
                            word = signResponse.word,
                            category = signResponse.category
                        )
                    }
                    errorMessage = null
                } else {
                    errorMessage = "Error al cargar señas: ${response.code()}"
                    // Usar datos de respaldo en caso de error
                    signs = getBackupSigns()
                }
            } catch (e: Exception) {
                errorMessage = "No se pudo conectar al servidor: ${e.message}"
                // Usar datos de respaldo
                signs = getBackupSigns()
            } finally {
                isLoading = false
            }
        }
    }
    
    val filteredSigns = remember(searchQuery, signs) {
        if (searchQuery.isBlank()) signs
        else signs.filter { 
            it.word.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Diccionario LSM",
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
        ) {
            // Error message (si existe)
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = AmarilloAdvertencia.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "⚠️ $errorMessage\nUsando datos de respaldo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmarilloAdvertencia,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            // Barra de búsqueda en Card profesional
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar palabra o categoría...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search, 
                            "Buscar",
                            tint = AzulTec
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulTec,
                        focusedLeadingIconColor = AzulTec,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
            
            // Loading con ShimmerEffect profesional
            if (isLoading) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(5) {
                        ShimmerEffect(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            isLoading = true,
                            contentAfterLoading = {}
                        )
                    }
                }
            }
            // Resultados
            else if (filteredSigns.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron resultados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrisOscuro.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSigns) { sign ->
                        var isVisible by remember { mutableStateOf(false) }
                        
                        LaunchedEffect(Unit) {
                            isVisible = true
                        }
                        
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                        ) {
                            SignCard(
                                sign = sign,
                                onClick = { onSignClick(sign.word) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SignCard(
    sign: Sign,
    onClick: () -> Unit
) {
    val iconData = getSignIcon(sign.word)
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono asociado a la palabra
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(iconData.backgroundColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconData.icon,
                    contentDescription = sign.word,
                    tint = iconData.iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sign.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulTec
                )
                Text(
                    text = sign.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisOscuro.copy(alpha = 0.6f)
                )
            }
        }
    }
}

data class SignIconData(
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color
)

// Función que retorna el icono apropiado para cada palabra
@Composable
fun getSignIcon(word: String): SignIconData {
    return when (word.lowercase()) {
        "hola" -> SignIconData(
            icon = Icons.Default.WavingHand,
            backgroundColor = AmarilloOro.copy(alpha = 0.15f),
            iconColor = AmarilloOro
        )
        "buenos días", "buenos dias" -> SignIconData(
            icon = Icons.Default.WbSunny,
            backgroundColor = NaranjaEnergia.copy(alpha = 0.15f),
            iconColor = NaranjaEnergia
        )
        "mamá", "mama" -> SignIconData(
            icon = Icons.Default.FamilyRestroom,
            backgroundColor = Color(0xFFF8BBD0).copy(alpha = 0.3f),
            iconColor = Color(0xFFEC407A)
        )
        "papá", "papa" -> SignIconData(
            icon = Icons.Default.Person,
            backgroundColor = AzulInfo.copy(alpha = 0.15f),
            iconColor = AzulInfo
        )
        "azul" -> SignIconData(
            icon = Icons.Default.Circle,
            backgroundColor = AzulTec.copy(alpha = 0.15f),
            iconColor = AzulTec
        )
        "ratón", "raton" -> SignIconData(
            icon = Icons.Default.Pets,
            backgroundColor = GrisOscuro.copy(alpha = 0.15f),
            iconColor = GrisOscuro
        )
        "amarillo" -> SignIconData(
            icon = Icons.Default.Circle,
            backgroundColor = Color(0xFFFFD54F).copy(alpha = 0.2f),
            iconColor = Color(0xFFFFD54F)
        )
        "rojo" -> SignIconData(
            icon = Icons.Default.Circle,
            backgroundColor = RojoError.copy(alpha = 0.15f),
            iconColor = RojoError
        )
        "verde" -> SignIconData(
            icon = Icons.Default.Circle,
            backgroundColor = VerdeExito.copy(alpha = 0.15f),
            iconColor = VerdeExito
        )
        "gato" -> SignIconData(
            icon = Icons.Default.Pets,
            backgroundColor = NaranjaEnergia.copy(alpha = 0.15f),
            iconColor = NaranjaEnergia
        )
        "perro" -> SignIconData(
            icon = Icons.Default.Pets,
            backgroundColor = VerdeExito.copy(alpha = 0.15f),
            iconColor = VerdeExito.copy(alpha = 0.8f)
        )
        else -> SignIconData(
            icon = Icons.Default.Translate,
            backgroundColor = AzulTec.copy(alpha = 0.1f),
            iconColor = AzulTec
        )
    }
}

// Datos de respaldo en caso de que el backend no esté disponible
private fun getBackupSigns(): List<Sign> {
    return listOf(
        Sign(11, "Amarillo", "Colores"),
        Sign(12, "Azul", "Colores"),
        Sign(13, "Blanco", "Colores"),
        Sign(14, "Café", "Colores"),
        Sign(15, "Gris", "Colores"),
        Sign(16, "Morado", "Colores"),
        Sign(17, "Naranja", "Colores"),
        Sign(18, "Negro", "Colores"),
        Sign(21, "Rojo", "Colores"),
        Sign(22, "Rosa", "Colores"),
        Sign(23, "Verde", "Colores"),
        Sign(24, "Abeja", "Animales"),
        Sign(25, "Águila", "Animales"),
        Sign(32, "Gato", "Animales"),
        Sign(34, "León", "Animales"),
        Sign(38, "Perro", "Animales"),
        Sign(39, "Tigre", "Animales"),
        Sign(42, "Vaca", "Animales")
    )
}
