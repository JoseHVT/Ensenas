package com.example.chat_bot.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Press Animation - Scale down cuando se presiona un botón
 * Target: Feedback táctil inmediato < 100ms
 */
fun Modifier.pressAnimation(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "press_scale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

/**
 * Bounce Animation - Rebote suave al entrar
 */
fun Modifier.bounceIn(
    delay: Int = 0,
    enabled: Boolean = true
): Modifier = composed {
    var isVisible by remember { mutableStateOf(!enabled) }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = delay),
        label = "bounce_alpha"
    )
    
    LaunchedEffect(enabled) {
        if (enabled) {
            kotlinx.coroutines.delay(delay.toLong())
            isVisible = true
        }
    }
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/**
 * Shimmer Effect - Loading animation elegante
 * 60 FPS optimized usando graphicsLayer
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    baseColor: Color = Color.LightGray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.5f),
    durationMillis: Int = 1500
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                // Hardware accelerated gradient translation
                translationX = size.width * offsetX
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(2f)
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            baseColor,
                            highlightColor,
                            baseColor
                        )
                    )
                )
        )
    }
}

/**
 * Fade In + Slide Up - Animación combinada para pantallas
 * Optimizada para 60 FPS
 */
fun enterTransition(
    delayMillis: Int = 0
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        )
    ) + slideInVertically(
        initialOffsetY = { it / 4 },
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        )
    )
}

/**
 * Fade Out + Slide Down
 */
fun exitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    ) + slideOutVertically(
        targetOffsetY = { -it / 4 },
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    )
}

/**
 * Pulse Animation - Para notificaciones y badges
 */
fun Modifier.pulse(enabled: Boolean = true): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
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
 * Shake Animation - Para errores o elementos bloqueados
 */
fun Modifier.shake(
    enabled: Boolean,
    onAnimationEnd: () -> Unit = {}
): Modifier = composed {
    var currentTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(enabled) {
        if (enabled) {
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 500) {
                currentTime = System.currentTimeMillis() - startTime
                kotlinx.coroutines.delay(16) // ~60 FPS
            }
            onAnimationEnd()
        }
    }
    
    val offsetX by animateDpAsState(
        targetValue = if (enabled) {
            val progress = (currentTime % 100) / 100f
            if (progress < 0.25f) 5.dp
            else if (progress < 0.75f) (-5).dp
            else 0.dp
        } else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "shake_offset"
    )
    
    this.offset(x = offsetX)
}

/**
 * Rotation Animation - Para loading spinners o iconos decorativos
 */
fun Modifier.infiniteRotation(
    enabled: Boolean = true,
    durationMillis: Int = 1000,
    clockwise: Boolean = true
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (clockwise) 360f else -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )
    
    if (enabled) {
        this.graphicsLayer {
            rotationZ = rotation
        }
    } else {
        this
    }
}

/**
 * Stagger Animation - Para listas
 * Cada item entra con delay progresivo
 */
@Composable
fun StaggeredAnimation(
    index: Int,
    baseDelay: Int = 50,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((baseDelay * index).toLong())
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition(0)
    ) {
        content()
    }
}

/**
 * Slide Transition - Para navegación entre screens
 */
@OptIn(ExperimentalAnimationApi::class)
fun slideTransition(
    direction: SlideDirection = SlideDirection.Left
): ContentTransform {
    val slideIn = slideInHorizontally(
        initialOffsetX = { fullWidth ->
            when (direction) {
                SlideDirection.Left -> fullWidth
                SlideDirection.Right -> -fullWidth
            }
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(
        animationSpec = tween(300)
    )
    
    val slideOut = slideOutHorizontally(
        targetOffsetX = { fullWidth ->
            when (direction) {
                SlideDirection.Left -> -fullWidth
                SlideDirection.Right -> fullWidth
            }
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(
        animationSpec = tween(300)
    )
    
    return slideIn togetherWith slideOut
}

enum class SlideDirection {
    Left,
    Right
}

/**
 * Scale Transition - Para elementos que aparecen/desaparecen
 */
fun scaleTransition(): ContentTransform {
    val scaleIn = scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(
        animationSpec = tween(200)
    )
    
    val scaleOut = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(200, easing = FastOutLinearInEasing)
    ) + fadeOut(
        animationSpec = tween(200)
    )
    
    return scaleIn togetherWith scaleOut
}

/**
 * Spring Bounce - Rebote al tocar
 */
fun Modifier.springBounce(
    targetScale: Float = 0.95f
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "spring_bounce"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

/**
 * Floating Animation - Movimiento sutil flotante
 */
fun Modifier.floating(
    enabled: Boolean = true,
    range: Float = 10f
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -range,
        targetValue = range,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )
    
    if (enabled) {
        this.offset { IntOffset(0, offsetY.roundToInt()) }
    } else {
        this
    }
}

/**
 * Glow Effect - Brillo pulsante para elementos destacados
 */
fun Modifier.glow(
    enabled: Boolean = true,
    glowColor: Color = Color.Yellow
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    if (enabled) {
        this.graphicsLayer {
            shadowElevation = 8f
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            this.alpha = alpha
        }
    } else {
        this
    }
}
