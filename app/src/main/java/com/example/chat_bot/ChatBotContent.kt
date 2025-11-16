package com.example.chat_bot

/**
 * Aquí viven TODOS los textos del chatbot.
 * Si después quieres cambiar explicaciones, SOLO editas este archivo.
 */

data class ChatBotOption(
    val id: String,
    val label: String,   // Texto que aparece en el botón del menú
    val title: String,   // Título de la respuesta
    val body: String     // Texto explicativo
)

object ChatBotContent {

    const val menuTitle = "¿En qué puedo ayudarte hoy? 🤖"

    // Opciones del menú principal
    val options = listOf(
        ChatBotOption(
            id = "start",
            label = "🧭 ¿Por dónde empiezo?",
            title = "¿Por dónde empiezo?",
            body = """
                ⭐ Paso 1: Abre la sección de módulos en la app.
                ⭐ Paso 2: Elige el primer módulo recomendado para ti.
                ⭐ Paso 3: Completa las lecciones en orden (video + práctica).
                
                💡 Consejo: No te preocupes por hacerlo perfecto a la primera.
                Lo importante es repetir las señas varias veces y repasar cuando lo necesites.
            """.trimIndent()
        ),
        ChatBotOption(
            id = "dictionary",
            label = "✋ Aprender Señas (Diccionario)",
            title = "Aprender Señas con el Diccionario",
            body = """
                📌 Paso 1: Entra a la sección de Diccionario.
                📌 Paso 2: Escribe la palabra que quieres aprender (ej. “hola”, “gracias”).
                📌 Paso 3: Reproduce el video y observa con calma el movimiento.
                
                💡 Consejo: Pausa el video y repite la seña frente a la pantalla 
                hasta que te sientas cómodo con el movimiento.
            """.trimIndent()
        ),
        ChatBotOption(
            id = "modules",
            label = "📘 Módulos de aprendizaje",
            title = "Módulos de aprendizaje",
            body = """
                🔹 Paso 1: Cada módulo agrupa varias lecciones sobre un tema 
                (saludos, colores, familia, etc.).
                🔹 Paso 2: Completa las lecciones en el orden sugerido.
                🔹 Paso 3: Al terminar, revisa tu porcentaje de avance.
                
                💡 Consejo: Intenta practicar un poquito cada día en lugar
                de hacer todo en una sola sesión.
            """.trimIndent()
        ),
        ChatBotOption(
            id = "quizzes",
            label = "📝 Quizzes y evaluaciones",
            title = "Quizzes y evaluaciones",
            body = """
                ✅ Paso 1: Después de practicar, entra a la sección de Quizzes.
                ✅ Paso 2: Responde según la seña que veas o la palabra mostrada.
                ✅ Paso 3: Al final verás tu calificación y en qué te equivocaste.
                
                💡 Consejo: Usa los quizzes para detectar en qué señas necesitas 
                reforzar más práctica.
            """.trimIndent()
        ),
        ChatBotOption(
            id = "memory",
            label = "🧠 Juego de Memoria",
            title = "Juego de Memoria",
            body = """
                🃏 Paso 1: Inicia una partida de memoria.
                🃏 Paso 2: Toca las tarjetas para descubrir la palabra y la seña.
                🃏 Paso 3: Intenta recordar las posiciones para formar las parejas.
                
                💡 Consejo: Este juego te ayuda a relacionar rápidamente palabra + seña
                mientras entrenas tu memoria visual.
            """.trimIndent()
        ),
        ChatBotOption(
            id = "progress",
            label = "📊 Mi progreso",
            title = "Mi progreso",
            body = """
                📈 Paso 1: Entra a tu sección de Progreso.
                📈 Paso 2: Revisa el porcentaje completado por módulo.
                📈 Paso 3: Observa tu tiempo total de práctica y las señas dominadas.
                
                💡 Consejo: Usa estas estadísticas para decidir qué módulo repasar
                y cuál puedes avanzar al siguiente nivel.
            """.trimIndent()
        ),
        ChatBotOption(
            id = "problems",
            label = "❗ Problemas comunes",
            title = "Problemas comunes",
            body = """
                ⚠️ Si algo no funciona como esperas, prueba esto:
                
                1️⃣ El video no carga:
                   • Revisa tu conexión a internet.
                   • Cierra y vuelve a abrir la app.
                
                2️⃣ No se guarda mi progreso:
                   • Verifica que hayas iniciado sesión.
                   • Asegúrate de completar la lección hasta el final.
                
                3️⃣ No sé qué hacer ahora:
                   • Vuelve al módulo anterior o usa esta guía para elegir tu siguiente paso.
            """.trimIndent()
        )
    )
}
