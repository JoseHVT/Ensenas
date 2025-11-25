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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ============================================
// DATA MODELS
// ============================================

data class QuizQuestion(
    val id: Int,
    val prompt: String,
    val options: List<String>,
    val correctAnswer: String,
    val videoUrl: String? = null,
    val type: QuizType
)

enum class QuizType {
    MULTIPLE_CHOICE_VIDEO,  // Mostrar video LSM, 4 opciones texto
    GESTURE_RECOGNITION,    // Mostrar palabra, seleccionar video correcto
    TRANSLATION,            // Mostrar texto, seleccionar video LSM
    SPEED_ROUND            // Preguntas rápidas con timer
}

data class QuizState(
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerCorrect: Boolean? = null,
    val lives: Int = 3,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val earnedXP: Int = 0,
    val timeRemaining: Int = 30, // Para Speed Round
    val isQuizComplete: Boolean = false,
    val isPerfect: Boolean = false
)

// ============================================
// MAIN QUIZ SCREEN
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizId: Int,
    onQuizComplete: (score: Int, xp: Int) -> Unit,
    onExit: () -> Unit
) {
    // TODO: Conectar con backend GET /quizzes/{quizId}
    // Por ahora usamos datos de prueba
    val questions = remember {
        listOf(
            QuizQuestion(
                id = 1,
                prompt = "¿Qué significa esta seña?",
                options = listOf("Hola", "Adiós", "Gracias", "Por favor"),
                correctAnswer = "Hola",
                videoUrl = "hola.mp4",
                type = QuizType.MULTIPLE_CHOICE_VIDEO
            ),
            QuizQuestion(
                id = 2,
                prompt = "Selecciona la seña para: 'Gracias'",
                options = listOf("Video 1", "Video 2", "Video 3", "Video 4"),
                correctAnswer = "Video 2",
                type = QuizType.GESTURE_RECOGNITION
            ),
            QuizQuestion(
                id = 3,
                prompt = "¿Cómo se dice 'Familia' en LSM?",
                options = listOf("Video A", "Video B", "Video C", "Video D"),
                correctAnswer = "Video B",
                type = QuizType.TRANSLATION
            ),
            QuizQuestion(
                id = 4,
                prompt = "¿Qué número es este?",
                options = listOf("5", "10", "15", "20"),
                correctAnswer = "10",
                videoUrl = "numero_10.mp4",
                type = QuizType.SPEED_ROUND
            ),
            QuizQuestion(
                id = 5,
                prompt = "Selecciona el color 'Azul'",
                options = listOf("Rojo", "Azul", "Verde", "Amarillo"),
                correctAnswer = "Azul",
                type = QuizType.MULTIPLE_CHOICE_VIDEO
            )
        )
    }

    var quizState by remember {
        mutableStateOf(
            QuizState(
                totalQuestions = questions.size
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }

    // Timer para Speed Round
    LaunchedEffect(quizState.currentQuestionIndex) {
        if (questions[quizState.currentQuestionIndex].type == QuizType.SPEED_ROUND) {
            while (quizState.timeRemaining > 0 && quizState.selectedAnswer == null) {
                delay(1000)
                quizState = quizState.copy(timeRemaining = quizState.timeRemaining - 1)
            }
            if (quizState.timeRemaining == 0 && quizState.selectedAnswer == null) {
                // Tiempo agotado, contar como incorrecto
                handleAnswer("", false, questions, quizState) { newState ->
                    quizState = newState
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quiz - Pregunta ${quizState.currentQuestionIndex + 1}/${questions.size}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.Close, "Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    // Lives display
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Vida",
                                tint = if (index < quizState.lives) Color.Red else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
                            Color.White
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (quizState.isQuizComplete) {
                QuizResultsScreen(
                    score = quizState.score,
                    totalQuestions = quizState.totalQuestions,
                    earnedXP = quizState.earnedXP,
                    isPerfect = quizState.isPerfect,
                    onContinue = { onQuizComplete(quizState.score, quizState.earnedXP) },
                    onReviewMistakes = { /* TODO */ }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Progress bar profesional
                    Column {
                        AnimatedProgressBar(
                            progress = (quizState.currentQuestionIndex + 1).toFloat() / questions.size,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            backgroundColor = Color(0xFFE5E7EB),
                            progressColor = VerdeExito
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score display con animaciones
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BouncingIcon(
                                    modifier = Modifier.size(24.dp),
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Puntuación",
                                            tint = AzulTec,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                )
                                Text(
                                    text = "${quizState.score}/${quizState.totalQuestions}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulTec
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "+${quizState.earnedXP}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = AmarilloOro
                                )
                                Text(
                                    text = "XP",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AmarilloOro.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question Content
                    QuestionContent(
                        question = questions[quizState.currentQuestionIndex],
                        selectedAnswer = quizState.selectedAnswer,
                        isAnswerCorrect = quizState.isAnswerCorrect,
                        timeRemaining = quizState.timeRemaining,
                        onAnswerSelected = { answer ->
                            if (quizState.selectedAnswer == null) {
                                val isCorrect = answer == questions[quizState.currentQuestionIndex].correctAnswer
                                handleAnswer(answer, isCorrect, questions, quizState) { newState ->
                                    quizState = newState
                                    
                                    // Auto-advance after 1.5 seconds
                                    coroutineScope.launch {
                                        delay(1500)
                                        if (newState.currentQuestionIndex < questions.size - 1) {
                                            quizState = newState.copy(
                                                currentQuestionIndex = newState.currentQuestionIndex + 1,
                                                selectedAnswer = null,
                                                isAnswerCorrect = null,
                                                timeRemaining = 30
                                            )
                                        } else {
                                            // Quiz complete
                                            checkQuizComplete(newState, questions) { finalState ->
                                                quizState = finalState
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // XP Reward Info
                    if (quizState.selectedAnswer == null) {
                        XPRewardInfo()
                    }
                }
            }
        }
    }

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Salir del quiz?") },
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
// QUESTION CONTENT
// ============================================

@Composable
private fun QuestionContent(
    question: QuizQuestion,
    selectedAnswer: String?,
    isAnswerCorrect: Boolean?,
    timeRemaining: Int,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timer for Speed Round
        if (question.type == QuizType.SPEED_ROUND && selectedAnswer == null) {
            SpeedRoundTimer(timeRemaining = timeRemaining)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Video Player (si aplica)
        if (question.videoUrl != null && 
            (question.type == QuizType.MULTIPLE_CHOICE_VIDEO || question.type == QuizType.SPEED_ROUND)) {
            VideoPlayerPlaceholder(videoUrl = question.videoUrl)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Question Text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = question.prompt,
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AzulTec,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer Options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            question.options.forEach { option ->
                AnswerOptionCard(
                    option = option,
                    isSelected = selectedAnswer == option,
                    isCorrect = if (selectedAnswer == option) isAnswerCorrect else null,
                    showCorrectAnswer = selectedAnswer != null && option == question.correctAnswer,
                    onClick = { if (selectedAnswer == null) onAnswerSelected(option) }
                )
            }
        }
    }
}

// ============================================
// ANSWER OPTION CARD
// ============================================

@Composable
private fun AnswerOptionCard(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    showCorrectAnswer: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected && isCorrect == true -> VerdeExito.copy(alpha = 0.2f)
        isSelected && isCorrect == false -> Color(0xFFFF4B4B).copy(alpha = 0.2f)
        showCorrectAnswer -> VerdeExito.copy(alpha = 0.1f)
        else -> Color.White
    }

    val borderColor = when {
        isSelected && isCorrect == true -> VerdeExito
        isSelected && isCorrect == false -> Color(0xFFFF4B4B)
        showCorrectAnswer -> VerdeExito
        else -> Color(0xFFE5E7EB)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected || showCorrectAnswer) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected && isCorrect == true -> VerdeExito
                    isSelected && isCorrect == false -> Color(0xFFFF4B4B)
                    showCorrectAnswer -> VerdeExito
                    else -> Color(0xFF374151)
                },
                modifier = Modifier.weight(1f)
            )

            // Icono con animación profesional
            when {
                isCorrect == true || showCorrectAnswer -> {
                    AnimatedCheckmark(
                        isVisible = true,
                        size = 32.dp,
                        color = VerdeExito
                    )
                }
                isCorrect == false -> {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color(0xFFFF4B4B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// ============================================
// VIDEO PLAYER PLACEHOLDER
// ============================================

@Composable
private fun VideoPlayerPlaceholder(videoUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = VerdeExito.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .padding(12.dp)
                )
                Text(
                    text = "Video: $videoUrl",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Toca para reproducir",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ============================================
// SPEED ROUND TIMER
// ============================================

@Composable
private fun SpeedRoundTimer(timeRemaining: Int) {
    val progress = timeRemaining / 30f
    val color = when {
        timeRemaining > 20 -> VerdeExito
        timeRemaining > 10 -> AmarilloOro
        else -> Color(0xFFFF4B4B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Timer circular profesional
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedCircularProgress(
                        progress = progress,
                        modifier = Modifier.size(60.dp),
                        size = 60.dp,
                        strokeWidth = 6.dp,
                        backgroundColor = color.copy(alpha = 0.2f),
                        progressColor = color,
                        showPercentage = false
                    )
                    Text(
                        text = "$timeRemaining",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                
                Column {
                    Text(
                        text = "RONDA RÁPIDA",
                        style = MaterialTheme.typography.labelLarge,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¡Responde rápido!",
                        style = MaterialTheme.typography.bodySmall,
                        color = color.copy(alpha = 0.7f)
                    )
                }
            }
            
            PulsingDot(
                color = color,
                size = 12.dp
            )
        }
    }
}

// ============================================
// XP REWARD INFO
// ============================================

@Composable
private fun XPRewardInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AmarilloOro.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BouncingIcon(
                modifier = Modifier.size(28.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = AmarilloOro,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
            Column {
                Text(
                    text = "Recompensas XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "+10 XP por respuesta correcta | +50 XP por quiz perfecto",
                    style = MaterialTheme.typography.bodySmall,
                    color = AmarilloOro,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============================================
// QUIZ RESULTS SCREEN
// ============================================

@Composable
private fun QuizResultsScreen(
    score: Int,
    totalQuestions: Int,
    earnedXP: Int,
    isPerfect: Boolean,
    onContinue: () -> Unit,
    onReviewMistakes: () -> Unit
) {
    val percentage = ((score.toFloat() / totalQuestions) * 100).roundToInt()
    val stars = when {
        percentage >= 90 -> 3
        percentage >= 70 -> 2
        percentage >= 50 -> 1
        else -> 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Celebration or Try Again
        if (isPerfect) {
            PerfectQuizCelebration()
        } else {
            Icon(
                imageVector = when (stars) {
                    3 -> Icons.Default.Star
                    2 -> Icons.Default.Star
                    1 -> Icons.Default.Star
                    else -> Icons.Default.Info
                },
                contentDescription = "Result",
                tint = when (stars) {
                    3 -> Color(0xFFFFC800)
                    2 -> Color(0xFFFF9600)
                    1 -> Color(0xFF0039A6)
                    else -> Color(0xFF6B7280)
                },
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isPerfect) "¡PERFECTO!" else when (stars) {
                3 -> "¡Excelente!"
                2 -> "¡Buen trabajo!"
                1 -> "¡Sigue practicando!"
                else -> "Inténtalo de nuevo"
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = AzulTec
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$score / $totalQuestions",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = VerdeExito
                )
                Text(
                    text = "respuestas correctas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                Spacer(modifier = Modifier.height(24.dp))

                // XP Earned
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = Color(0xFFFFC800),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "+$earnedXP XP",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC800)
                    )
                }

                if (isPerfect) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¡Bonus de quiz perfecto!",
                        style = MaterialTheme.typography.bodySmall,
                        color = VerdeExito,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeExito
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "CONTINUAR",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (score < totalQuestions) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onReviewMistakes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, AzulTec)
            ) {
                Text(
                    text = "REVISAR ERRORES",
                    fontWeight = FontWeight.Bold,
                    color = AzulTec,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ============================================
// PERFECT QUIZ CELEBRATION
// ============================================

@Composable
private fun PerfectQuizCelebration() {
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

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Perfect",
            tint = Color(0xFFFFC800),
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation)
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Perfect",
            tint = VerdeExito,
            modifier = Modifier.size(80.dp)
        )
    }
}

// ============================================
// HELPER FUNCTIONS
// ============================================

private fun handleAnswer(
    answer: String,
    isCorrect: Boolean,
    questions: List<QuizQuestion>,
    currentState: QuizState,
    onStateUpdate: (QuizState) -> Unit
) {
    val newLives = if (isCorrect) currentState.lives else (currentState.lives - 1).coerceAtLeast(0)
    val newScore = if (isCorrect) currentState.score + 1 else currentState.score
    val xpForThisAnswer = if (isCorrect) 10 else 0
    val newXP = currentState.earnedXP + xpForThisAnswer

    onStateUpdate(
        currentState.copy(
            selectedAnswer = answer,
            isAnswerCorrect = isCorrect,
            lives = newLives,
            score = newScore,
            earnedXP = newXP
        )
    )
}

private fun checkQuizComplete(
    currentState: QuizState,
    questions: List<QuizQuestion>,
    onStateUpdate: (QuizState) -> Unit
) {
    val isPerfect = currentState.score == questions.size && currentState.lives == 3
    val bonusXP = when {
        isPerfect -> 100
        currentState.lives == 3 -> 50
        else -> 0
    }
    
    onStateUpdate(
        currentState.copy(
            isQuizComplete = true,
            isPerfect = isPerfect,
            earnedXP = currentState.earnedXP + bonusXP
        )
    )
}
