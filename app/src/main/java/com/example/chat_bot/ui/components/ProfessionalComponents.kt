package com.example.chat_bot.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chat_bot.ui.theme.*
import kotlin.math.sin

/**
 * Shimmer Loading Effect - Estilo profesional para estados de carga
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    contentAfterLoading: @Composable () -> Unit
) {
    if (isLoading) {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslate"
        )
        
        Box(
            modifier = modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GrisClaro.copy(alpha = 0.3f),
                            GrisClaro.copy(alpha = 0.5f),
                            GrisClaro.copy(alpha = 0.3f)
                        ),
                        start = Offset(translateAnim - 300f, 0f),
                        end = Offset(translateAnim, 0f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )
    } else {
        contentAfterLoading()
    }
}

/**
 * Animated Progress Bar - Barra de progreso con animación fluida
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = GrisClaro,
    progressColor: Color = VerdeExito,
    height: Dp = 8.dp,
    animationDuration: Int = 1000
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "progressAnimation"
    )
    
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            progressColor,
                            progressColor.copy(alpha = 0.8f)
                        )
                    )
                )
        )
    }
}

/**
 * Animated Circular Progress - Progreso circular estilo moderno
 */
@Composable
fun AnimatedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    backgroundColor: Color = GrisClaro.copy(alpha = 0.3f),
    progressColor: Color = VerdeExito,
    showPercentage: Boolean = true,
    animationDuration: Int = 1000
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "circularProgress"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.toPx()
            val radius = diameter / 2
            val strokeWidthPx = strokeWidth.toPx()
            
            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = radius - strokeWidthPx / 2,
                style = Stroke(width = strokeWidthPx)
            )
            
            // Progress arc
            val sweepAngle = 360 * animatedProgress
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx),
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = androidx.compose.ui.geometry.Size(
                    diameter - strokeWidthPx,
                    diameter - strokeWidthPx
                )
            )
        }
        
        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
        }
    }
}

/**
 * Bouncing Icon - Ícono con animación de rebote
 */
@Composable
fun BouncingIcon(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceScale"
    )
    
    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

/**
 * Gradient Card - Tarjeta con gradiente profesional
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradient: Brush = Brush.linearGradient(
        colors = listOf(AzulTec, AzulTecLight)
    ),
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

/**
 * Pulsing Dot Indicator - Indicador animado estilo "en línea"
 */
@Composable
fun PulsingDot(
    color: Color = VerdeExito,
    size: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

/**
 * Success Checkmark Animation - Checkmark animado de éxito
 */
@Composable
fun AnimatedCheckmark(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = VerdeExito,
    size: Dp = 60.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmarkScale"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val checkPath = Path().apply {
                moveTo(canvasWidth * 0.25f, canvasHeight * 0.5f)
                lineTo(canvasWidth * 0.4f, canvasHeight * 0.7f)
                lineTo(canvasWidth * 0.75f, canvasHeight * 0.3f)
            }
            drawPath(
                path = checkPath,
                color = color,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
