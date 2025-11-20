package com.example.chat_bot.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AzulTec,
    onPrimary = BlancoTec,
    secondary = AzulTecClaro,
    onSecondary = BlancoTec,
    tertiary = AzulInfo,
    background = FondoOscuro,
    surface = TarjetaOscura,
    onBackground = BlancoTec,
    onSurface = BlancoTec,
    error = RojoError,
    onError = BlancoTec
)

private val LightColorScheme = lightColorScheme(
    primary = AzulTec,
    onPrimary = BlancoTec,
    secondary = AzulTecClaro,
    onSecondary = BlancoTec,
    tertiary = AzulInfo,
    background = FondoClaro,
    surface = TarjetaClara,
    onBackground = GrisOscuro,
    onSurface = GrisOscuro,
    error = RojoError,
    onError = BlancoTec
)

@Composable
fun EnsenasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AzulTec.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Mantener compatibilidad con código antiguo
@Composable
fun Chat_BotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = EnsenasTheme(darkTheme, content)
