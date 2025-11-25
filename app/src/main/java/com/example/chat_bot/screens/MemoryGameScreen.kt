package com.example.chat_bot.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

// ============================================
// DATA MODELS
// ============================================

data class MemoryCard(
    val id: Int,
    val pairId: Int,
    val word: String,
    val videoThumbnail: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

enum class DifficultyLevel(
    val displayName: String,
    val gridSize: Int,
    val pairs: Int,
    val color: Color,
    val timeBonus: Int
) {
    EASY("Fácil", 4, 8, Color(0xFF58CC02), 120),
    MEDIUM("Medio", 6, 18, Color(0xFFFF9600), 180),
    HARD("Difícil", 8, 32, Color(0xFFFF4B4B), 240)
}

data class GameState(
    val cards: List<MemoryCard> = emptyList(),
    val flippedCards: List<MemoryCard> = emptyList(),
    val matchedPairs: Int = 0,
    val moves: Int = 0,
    val timeElapsed: Int = 0,
    val isGameComplete: Boolean = false,
    val stars: Int = 0,
    val isProcessing: Boolean = false
)

// ============================================
// MEMORY GAME SCREEN
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen(
    difficulty: DifficultyLevel = DifficultyLevel.EASY,
    onGameComplete: (stars: Int, moves: Int, time: Int) -> Unit,
    onExit: () -> Unit
) {
    var gameState by remember { mutableStateOf(GameState()) }
    val coroutineScope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }
    var showDifficultySelector by remember { mutableStateOf(false) }
    var currentDifficulty by remember { mutableStateOf(difficulty) }

    // Initialize game
    LaunchedEffect(currentDifficulty) {
        gameState = initializeGame(currentDifficulty)
    }

    // Timer
    LaunchedEffect(gameState.isGameComplete) {
        while (!gameState.isGameComplete) {
            delay(1000)
            gameState = gameState.copy(timeElapsed = gameState.timeElapsed + 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Juego de Memoria",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "${currentDifficulty.displayName} - ${currentDifficulty.pairs} pares",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.Close, "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showDifficultySelector = true }) {
                        Icon(Icons.Default.Settings, "Dificultad", tint = Color.White)
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
            if (gameState.isGameComplete) {
                GameCompleteScreen(
                    difficulty = currentDifficulty,
                    moves = gameState.moves,
                    timeElapsed = gameState.timeElapsed,
                    stars = gameState.stars,
                    onPlayAgain = {
                        gameState = initializeGame(currentDifficulty)
                    },
                    onChangeDifficulty = {
                        showDifficultySelector = true
                    },
                    onExit = onExit
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Stats Row
                    GameStatsRow(
                        matchedPairs = gameState.matchedPairs,
                        totalPairs = currentDifficulty.pairs,
                        moves = gameState.moves,
                        timeElapsed = gameState.timeElapsed
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Game Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(currentDifficulty.gridSize / 2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(gameState.cards.size) { index ->
                            MemoryCardItem(
                                card = gameState.cards[index],
                                onClick = {
                                    if (!gameState.isProcessing && 
                                        gameState.flippedCards.size < 2 &&
                                        !gameState.cards[index].isFlipped &&
                                        !gameState.cards[index].isMatched
                                    ) {
                                        coroutineScope.launch {
                                            handleCardClick(
                                                cardIndex = index,
                                                currentState = gameState,
                                                difficulty = currentDifficulty,
                                                onStateUpdate = { newState ->
                                                    gameState = newState
                                                },
                                                onGameComplete = { stars ->
                                                    onGameComplete(stars, gameState.moves, gameState.timeElapsed)
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Difficulty Selector Dialog
    if (showDifficultySelector) {
        DifficultyDialog(
            currentDifficulty = currentDifficulty,
            onDifficultySelected = { newDifficulty ->
                currentDifficulty = newDifficulty
                showDifficultySelector = false
            },
            onDismiss = { showDifficultySelector = false }
        )
    }

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Salir del juego?") },
            text = { Text("Perderás tu progreso actual") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onExit()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continuar")
                }
            }
        )
    }
}

// ============================================
// GAME STATS ROW
// ============================================

@Composable
private fun GameStatsRow(
    matchedPairs: Int,
    totalPairs: Int,
    moves: Int,
    timeElapsed: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(
            icon = Icons.Default.CheckCircle,
            title = "Pares",
            value = "$matchedPairs/$totalPairs",
            color = VerdeExito,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        StatCard(
            icon = Icons.Default.Favorite,
            title = "Movimientos",
            value = moves.toString(),
            color = Color(0xFFFF9600),
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        StatCard(
            icon = Icons.Default.Add,
            title = "Tiempo",
            value = formatTime(timeElapsed),
            color = AzulTec,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280),
                fontSize = 10.sp
            )
        }
    }
}

// ============================================
// MEMORY CARD ITEM
// ============================================

@Composable
private fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (card.isMatched) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        colors = CardDefaults.cardColors(
            containerColor = if (card.isMatched) VerdeExito.copy(alpha = 0.3f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (card.isMatched) BorderStroke(2.dp, VerdeExito) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation > 90f || card.isMatched) {
                // Front (flipped) - show content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Video thumbnail placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1F2937)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = card.word,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (card.isMatched) VerdeExito else AzulTec,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            } else {
                // Back (not flipped) - show logo
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Card back",
                    tint = AzulTec.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

// ============================================
// DIFFICULTY SELECTOR DIALOG
// ============================================

@Composable
private fun DifficultyDialog(
    currentDifficulty: DifficultyLevel,
    onDifficultySelected: (DifficultyLevel) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Selecciona Dificultad",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DifficultyLevel.values().forEach { level ->
                    DifficultyOption(
                        level = level,
                        isSelected = level == currentDifficulty,
                        onClick = { onDifficultySelected(level) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun DifficultyOption(
    level: DifficultyLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) level.color.copy(alpha = 0.2f) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, level.color) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = level.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = level.color
                )
                Text(
                    text = "Grid ${level.gridSize/2}x${level.gridSize/2} • ${level.pairs} pares",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = level.color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ============================================
// GAME COMPLETE SCREEN
// ============================================

@Composable
private fun GameCompleteScreen(
    difficulty: DifficultyLevel,
    moves: Int,
    timeElapsed: Int,
    stars: Int,
    onPlayAgain: () -> Unit,
    onChangeDifficulty: () -> Unit,
    onExit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = if (index < stars) Color(0xFFFFC800) else Color(0xFFE5E7EB),
                    modifier = Modifier
                        .size(64.dp)
                        .rotate(if (index < stars) rotation else 0f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Juego Completado!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = VerdeExito
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = difficulty.color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = difficulty.displayName.uppercase(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = difficulty.color,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStat(
                        icon = Icons.Default.Favorite,
                        label = "Movimientos",
                        value = moves.toString(),
                        color = Color(0xFFFF9600)
                    )
                    ResultStat(
                        icon = Icons.Default.Add,
                        label = "Tiempo",
                        value = formatTime(timeElapsed),
                        color = AzulTec
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                // Performance message
                Text(
                    text = when (stars) {
                        3 -> "¡Rendimiento Excepcional! 🏆"
                        2 -> "¡Buen Trabajo! 🎯"
                        1 -> "¡Bien Hecho! ⭐"
                        else -> "¡Sigue Practicando! 💪"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = difficulty.color,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeExito
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "JUGAR DE NUEVO",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onChangeDifficulty,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, AzulTec)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = AzulTec,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CAMBIAR DIFICULTAD",
                fontWeight = FontWeight.Bold,
                color = AzulTec,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Salir",
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun ResultStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280)
        )
    }
}

// ============================================
// HELPER FUNCTIONS
// ============================================

private fun initializeGame(difficulty: DifficultyLevel): GameState {
    // TODO: Fetch from backend GET /memory/deck?size=${difficulty.pairs}
    // Por ahora generamos datos de prueba
    
    val words = listOf(
        "Hola", "Adiós", "Gracias", "Por favor", "Sí", "No",
        "Buenos días", "Buenas noches", "Familia", "Amigo",
        "Casa", "Escuela", "Trabajo", "Comer", "Beber",
        "Dormir", "Caminar", "Correr", "Leer", "Escribir",
        "Feliz", "Triste", "Enojado", "Sorprendido", "Miedo",
        "Amor", "Paz", "Ayuda", "Perdón", "Bien",
        "Mal", "Grande", "Pequeño"
    )

    val pairs = mutableListOf<MemoryCard>()
    var cardId = 0
    
    for (pairId in 0 until difficulty.pairs) {
        val word = words.getOrNull(pairId) ?: "Palabra $pairId"
        // Create two cards for each pair
        pairs.add(
            MemoryCard(
                id = cardId++,
                pairId = pairId,
                word = word,
                videoThumbnail = "thumb_$pairId.jpg"
            )
        )
        pairs.add(
            MemoryCard(
                id = cardId++,
                pairId = pairId,
                word = word,
                videoThumbnail = "thumb_$pairId.jpg"
            )
        )
    }

    return GameState(
        cards = pairs.shuffled()
    )
}

private suspend fun handleCardClick(
    cardIndex: Int,
    currentState: GameState,
    difficulty: DifficultyLevel,
    onStateUpdate: (GameState) -> Unit,
    onGameComplete: (Int) -> Unit
) {
    val updatedCards = currentState.cards.toMutableList()
    updatedCards[cardIndex] = updatedCards[cardIndex].copy(isFlipped = true)
    
    val newFlippedCards = currentState.flippedCards + updatedCards[cardIndex]
    
    onStateUpdate(
        currentState.copy(
            cards = updatedCards,
            flippedCards = newFlippedCards,
            isProcessing = true
        )
    )

    if (newFlippedCards.size == 2) {
        delay(800) // Delay para que el usuario vea las cartas
        
        val isMatch = newFlippedCards[0].pairId == newFlippedCards[1].pairId
        
        if (isMatch) {
            // Match found
            val cardsAfterMatch = updatedCards.map { card ->
                if (card.pairId == newFlippedCards[0].pairId) {
                    card.copy(isMatched = true)
                } else {
                    card
                }
            }
            
            val newMatchedPairs = currentState.matchedPairs + 1
            val newMoves = currentState.moves + 1
            
            // Check if game is complete
            if (newMatchedPairs == difficulty.pairs) {
                val stars = calculateStars(newMoves, currentState.timeElapsed, difficulty)
                onStateUpdate(
                    currentState.copy(
                        cards = cardsAfterMatch,
                        flippedCards = emptyList(),
                        matchedPairs = newMatchedPairs,
                        moves = newMoves,
                        isGameComplete = true,
                        stars = stars,
                        isProcessing = false
                    )
                )
                onGameComplete(stars)
            } else {
                onStateUpdate(
                    currentState.copy(
                        cards = cardsAfterMatch,
                        flippedCards = emptyList(),
                        matchedPairs = newMatchedPairs,
                        moves = newMoves,
                        isProcessing = false
                    )
                )
            }
        } else {
            // No match - flip back
            val cardsFlippedBack = updatedCards.map { card ->
                if (card.id == newFlippedCards[0].id || card.id == newFlippedCards[1].id) {
                    card.copy(isFlipped = false)
                } else {
                    card
                }
            }
            
            onStateUpdate(
                currentState.copy(
                    cards = cardsFlippedBack,
                    flippedCards = emptyList(),
                    moves = currentState.moves + 1,
                    isProcessing = false
                )
            )
        }
    } else {
        onStateUpdate(
            currentState.copy(
                cards = updatedCards,
                flippedCards = newFlippedCards,
                isProcessing = false
            )
        )
    }
}

private fun calculateStars(moves: Int, timeElapsed: Int, difficulty: DifficultyLevel): Int {
    val optimalMoves = difficulty.pairs + (difficulty.pairs / 2)
    val optimalTime = difficulty.timeBonus
    
    return when {
        moves <= optimalMoves && timeElapsed <= optimalTime -> 3
        moves <= optimalMoves * 1.5 && timeElapsed <= optimalTime * 1.5 -> 2
        else -> 1
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}
