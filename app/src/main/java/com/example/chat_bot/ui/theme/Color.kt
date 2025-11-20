package com.example.chat_bot.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================
// COLORES PRINCIPALES - TEC DE MONTERREY
// ============================================

val AzulTec = Color(0xFF0039A6)           // Azul Tec Principal
val AzulTecLight = Color(0xFF4A90E2)      // Azul claro para highlights
val AzulTecDark = Color(0xFF002366)       // Azul oscuro para depth
val BlancoTec = Color(0xFFFFFFFF)

// ============================================
// GAMIFICACIÓN - ESTILO DUOLINGO
// ============================================

val VerdeExito = Color(0xFF58CC02)        // Verde brillante - Éxito
val VerdeExitoLight = Color(0xFF89E219)   // Verde claro
val AmarilloOro = Color(0xFFFFC800)       // Oro para racha/XP
val NaranjaEnergia = Color(0xFFFF9600)    // Naranja para notificaciones
val RojoError = Color(0xFFFF4B4B)         // Rojo suave - Errores
val AzulInfo = Color(0xFF1CB0F6)          // Azul información

// ============================================
// NEUTRALES
// ============================================

val Blanco = Color(0xFFFFFFFF)            // Blanco puro
val BlancoNieve = Color(0xFFF7F8FA)       // Fondo claro
val BlancoFondo = Color(0xFFFAFAFA)       // Fondo claro alternativo
val GrisClaro = Color(0xFFE5E7EB)         // Bordes sutiles
val GrisMedio = Color(0xFF9CA3AF)         // Texto secundario
val GrisOscuro = Color(0xFF374151)        // Texto principal
val Negro = Color(0xFF1F2937)             // Negro suave
val NegroTexto = Color(0xFF1F2937)        // Negro para texto
val NegroFondo = Color(0xFF121212)        // Negro para fondos

// ============================================
// FONDOS
// ============================================

val FondoClaro = Color(0xFFFAFAFA)
val FondoOscuro = Color(0xFF121212)
val TarjetaClara = Color(0xFFFFFFFF)
val TarjetaOscura = Color(0xFF1E1E1E)

// ============================================
// ESTADOS
// ============================================

val Deshabilitado = Color(0xFFBDBDBD)
val Seleccionado = AzulTec
val NoSeleccionado = Color(0xFF757575)

// ============================================
// GRADIENTES
// ============================================

val AzulGradient = Brush.verticalGradient(
    colors = listOf(AzulTec, AzulTecLight)
)

val VerdeGradient = Brush.verticalGradient(
    colors = listOf(VerdeExito, VerdeExitoLight)
)

val OroGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFD700), AmarilloOro)
)

val FondoGradient = Brush.verticalGradient(
    colors = listOf(BlancoNieve, GrisClaro)
)

// ============================================
// COLORES POR CATEGORÍA - MÓDULOS
// ============================================

val CategoriaBasico = VerdeExito
val CategoriaIntermedio = NaranjaEnergia
val CategoriaAvanzado = AzulTec

// ============================================
// COLORES ADICIONALES - UI
// ============================================

val AzulClaro = Color(0xFFE3F2FD)         // Azul muy claro
val AmarilloAdvertencia = Color(0xFFFFC107) // Amarillo advertencia
val RojoAdvertencia = Color(0xFFF44336)   // Rojo advertencia
val NaranjaFuego = Color(0xFFFF5722)      // Naranja fuego para racha
val DoradoGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFD700), AmarilloOro, Color(0xFFFFD700))
)
val ArcoirisGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFF6B6B), 
        Color(0xFFFFD93D), 
        Color(0xFF6BCB77), 
        Color(0xFF4D96FF)
    )
)
val AzulTecClaro = Color(0xFF4A7EBB)
val AzulTecOscuro = Color(0xFF002D72)

