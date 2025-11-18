package com.example.chat_bot.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar palabra o categoría...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, "Buscar")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulTec,
                    focusedLeadingIconColor = AzulTec
                )
            )
            
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AzulTec)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando diccionario...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisOscuro
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

@Composable
fun SignCard(
    sign: Sign,
    onClick: () -> Unit
) {
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
            // Placeholder para thumbnail (después será imagen real)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(AzulTec.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sign.word.first().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = AzulTec,
                    fontWeight = FontWeight.Bold
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
