package com.example.chat_bot.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.chat_bot.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Shimmer Loading Card - Skeleton screen optimizado
 * Hardware accelerated usando graphicsLayer
 */
@Composable
fun ShimmerLoadingCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
        
        val translateAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translate"
        )
        
        val brush = Brush.horizontalGradient(
            colors = listOf(
                GrisClaro.copy(alpha = 0.3f),
                BlancoNieve,
                GrisClaro.copy(alpha = 0.3f)
            ),
            startX = translateAnim - 500f,
            endX = translateAnim
        )
        
        Box(
            modifier = modifier
                .background(
                    brush = brush,
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}

/**
 * Shimmer List - Multiple skeleton items
 */
@Composable
fun ShimmerList(
    itemCount: Int = 5,
    itemHeight: androidx.compose.ui.unit.Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            ShimmerLoadingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
            )
        }
    }
}

/**
 * Animated Progress Bar con gradiente
 * Optimizado para 60 FPS
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(VerdeExito, VerdeExitoLight)
    ),
    trackColor: Color = GrisClaro,
    animationDurationMs: Int = 800
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress = 0f
        delay(100) // Pequeño delay para mejor percepción
        animatedProgress = progress
    }
    
    val currentProgress by animateFloatAsState(
        targetValue = animatedProgress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "progress_animation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(currentProgress)
                .background(gradient, RoundedCornerShape(6.dp))
        )
    }
}

/**
 * Circular Progress Ring con animación
 */
@Composable
fun AnimatedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: androidx.compose.ui.unit.Dp = 8.dp,
    color: Color = VerdeExito,
    trackColor: Color = GrisClaro,
    animationDurationMs: Int = 1000
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress = progress
    }
    
    val currentProgress by animateFloatAsState(
        targetValue = animatedProgress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "circular_progress"
    )
    
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        val strokeWidthPx = strokeWidth.toPx()
        
        // Track (fondo)
        drawCircle(
            color = trackColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidthPx)
        )
        
        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * currentProgress,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidthPx,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

/**
 * Count Up Animation - Para números que incrementan
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    durationMs: Int = 800,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
    color: Color = AzulTec
) {
    var count by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(targetValue) {
        val startValue = count
        val diff = targetValue - startValue
        val steps = 30
        val stepDelay = durationMs / steps
        
        for (i in 1..steps) {
            delay(stepDelay.toLong())
            count = startValue + (diff * i / steps)
        }
        count = targetValue
    }
    
    androidx.compose.material3.Text(
        text = "$count",
        style = textStyle,
        color = color,
        modifier = modifier
    )
}

/**
 * Typing Animation - Para texto que aparece letra por letra
 */
@Composable
fun TypingText(
    text: String,
    modifier: Modifier = Modifier,
    delayPerChar: Long = 50,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
    color: Color = GrisOscuro
) {
    var visibleText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        visibleText = ""
        text.forEachIndexed { index, _ ->
            delay(delayPerChar)
            visibleText = text.substring(0, index + 1)
        }
    }
    
    androidx.compose.material3.Text(
        text = visibleText,
        style = textStyle,
        color = color,
        modifier = modifier
    )
}

/**
 * Fade Crossfade - Transición suave entre contenidos
 */
@Composable
fun <T> FadeCrossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(300),
    content: @Composable (T) -> Unit
) {
    Crossfade(
        targetState = targetState,
        animationSpec = animationSpec,
        modifier = modifier,
        label = "fade_crossfade"
    ) { state ->
        content(state)
    }
}

/**
 * Particle Celebration - Confetti animation
 */
@Composable
fun ParticleCelebration(
    isActive: Boolean,
    particleCount: Int = 20,
    colors: List<Color> = listOf(
        AmarilloOro,
        VerdeExito,
        AzulTec,
        NaranjaEnergia
    )
) {
    if (isActive) {
        val particles = remember {
            List(particleCount) {
                Particle(
                    color = colors.random(),
                    initialX = (0..100).random().toFloat(),
                    initialY = (0..100).random().toFloat(),
                    velocityX = (-50..50).random().toFloat(),
                    velocityY = (-100..-50).random().toFloat()
                )
            }
        }
        
        // TODO: Implementar rendering de partículas con Canvas
        // Por ahora solo placeholder
    }
}

private data class Particle(
    val color: Color,
    val initialX: Float,
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float
)

/**
 * Wave Animation - Para fondos dinámicos
 */
@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    waveColor: Color = AzulTec.copy(alpha = 0.1f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )
    
    // Placeholder - implementación completa requiere Canvas custom
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        waveColor,
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Breathing Animation - Sutil scale up/down
 */
fun Modifier.breathing(enabled: Boolean = true): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )
    
    if (enabled) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}

/**
 * Reveal Animation - Aparece desde el centro
 */
@Composable
fun RevealAnimation(
    visible: Boolean,
    durationMs: Int = 400,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMs)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(
                durationMillis = durationMs,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(tween(durationMs / 2)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(
                durationMillis = durationMs / 2,
                easing = FastOutLinearInEasing
            )
        )
    ) {
        content()
    }
}

/**
 * PressableCard - Card interactivo con animación al presionar
 * Estilo profesional con feedback táctil inmediato
 */
@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    pressScale: Float = 0.97f,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val haptic = rememberHapticFeedback()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_press_scale"
    )
    
    val pressElevation by animateDpAsState(
        targetValue = if (isPressed && enabled) elevation + 2.dp else elevation,
        animationSpec = tween(100),
        label = "card_press_elevation"
    )
    
    androidx.compose.material3.Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { 
                            haptic.light()
                            onClick()
                        }
                    )
                }
            },
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = pressElevation
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(content = content)
    }
}
