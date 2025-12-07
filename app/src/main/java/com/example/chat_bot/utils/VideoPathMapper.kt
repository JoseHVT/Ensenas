package com.example.chat_bot.utils

/**
 * Mapea palabras LSM a sus rutas de video correspondientes
 * Basado en la estructura de carpetas: Proyecto LSM - Videos e Imágenes AD 2022_5
 */
object VideoPathMapper {
    
    // Mapa de palabras a archivos de video
    private val videoMap = mapOf(
        // Abecedario
        "a" to "LSM_Abecedario_Web/a.JPG",
        "b" to "LSM_Abecedario_Web/b.JPG",
        "c" to "LSM_Abecedario_Web/c.JPG",
        "d" to "LSM_Abecedario_Web/d.JPG",
        "e" to "LSM_Abecedario_Web/e.JPG",
        "f" to "LSM_Abecedario_Web/f.JPG",
        "g" to "LSM_Abecedario_Web/g.JPG",
        "h" to "LSM_Abecedario_Web/h.JPG",
        "i" to "LSM_Abecedario_Web/i.JPG",
        "j" to "LSM_Abecedario_Web/J_Web.m4v",
        "k" to "LSM_Abecedario_Web/K_Web.m4v",
        "l" to "LSM_Abecedario_Web/L_Web.m4v",
        "m" to "LSM_Abecedario_Web/M_Web.m4v",
        "n" to "LSM_Abecedario_Web/N_Web.m4v",
        "ñ" to "LSM_Abecedario_Web/Enie_Web.m4v",
        "o" to "LSM_Abecedario_Web/O_Web.m4v",
        "p" to "LSM_Abecedario_Web/P_Web.m4v",
        "q" to "LSM_Abecedario_Web/Q_Web.m4v",
        "r" to "LSM_Abecedario_Web/R_Web.m4v",
        "s" to "LSM_Abecedario_Web/S_Web.m4v",
        "t" to "LSM_Abecedario_Web/T_Web.m4v",
        "u" to "LSM_Abecedario_Web/U_Web.m4v",
        "v" to "LSM_Abecedario_Web/V_Web.m4v",
        "w" to "LSM_Abecedario_Web/W_Web.m4v",
        "x" to "LSM_Abecedario_Web/X_Web.m4v",
        "y" to "LSM_Abecedario_Web/Y_Web.m4v",
        "z" to "LSM_Abecedario_Web/Z_Web.m4v",
        
        // Números
        "uno" to "LSM_Numero_Web/Uno_Web.m4v",
        "dos" to "LSM_Numero_Web/Dos_Web.m4v",
        "tres" to "LSM_Numero_Web/Tres_Web.m4v",
        "cuatro" to "LSM_Numero_Web/Cuatro_Web.m4v",
        "cinco" to "LSM_Numero_Web/Cinco_Web.m4v",
        "seis" to "LSM_Numero_Web/Seis_Web.m4v",
        "siete" to "LSM_Numero_Web/Siete_Web.m4v",
        "ocho" to "LSM_Numero_Web/Ocho_Web.m4v",
        "nueve" to "LSM_Numero_Web/Nueve_Web.m4v",
        "diez" to "LSM_Numero_Web/Diez_Web.m4v",
        
        // Colores
        "amarillo" to "LSM_Colores_Web/Amarillo_Web.m4v",
        "azul" to "LSM_Colores_Web/Azul_Web.m4v",
        "blanco" to "LSM_Colores_Web/Blanco_Web.m4v",
        "cafe" to "LSM_Colores_Web/Cafe_Web.m4v",
        "café" to "LSM_Colores_Web/Cafe_Web.m4v",
        "gris" to "LSM_Colores_Web/Gris_Web.m4v",
        "morado" to "LSM_Colores_Web/Morado_Web.m4v",
        "naranja" to "LSM_Colores_Web/Naranja_Web.m4v",
        "negro" to "LSM_Colores_Web/Negro_Web.m4v",
        "rojo" to "LSM_Colores_Web/Rojo_Web.m4v",
        "rosa" to "LSM_Colores_Web/Rosa_Web.m4v",
        "verde" to "LSM_Colores_Web/Verde_Web.m4v",
        
        // Animales
        "abeja" to "LSM_Animales_Web/Abeja_Web.m4v",
        "aguila" to "LSM_Animales_Web/Aguila_Web.m4v",
        "águila" to "LSM_Animales_Web/Aguila_Web.m4v",
        "gato" to "LSM_Animales_Web/Gato_Web.m4v",
        "leon" to "LSM_Animales_Web/Leon_Web.m4v",
        "león" to "LSM_Animales_Web/Leon_Web.m4v",
        "perro" to "LSM_Animales_Web/Perro_Web.m4v",
        "raton" to "LSM_Animales_Web/Raton_Web.m4v",
        "ratón" to "LSM_Animales_Web/Raton_Web.m4v",
        "tigre" to "LSM_Animales_Web/Tigre_Web.m4v",
        "vaca" to "LSM_Animales_Web/Vaca_Web.m4v",
        
        // Saludos y Cortesías
        "hola" to "LSM_SaludosCortesias_Web/Hola_Web.m4v",
        "adios" to "LSM_SaludosCortesias_Web/Adios_Web.m4v",
        "adiós" to "LSM_SaludosCortesias_Web/Adios_Web.m4v",
        "buenos dias" to "LSM_SaludosCortesias_Web/Buenosdias_Web.m4v",
        "buenos días" to "LSM_SaludosCortesias_Web/Buenosdias_Web.m4v",
        "buenas noches" to "LSM_SaludosCortesias_Web/Buenasnoches_Web.m4v",
        "buenas tardes" to "LSM_SaludosCortesias_Web/Buenastardes_Web.m4v",
        "gracias" to "LSM_SaludosCortesias_Web/Gracias_Web.m4v",
        "perdon" to "LSM_SaludosCortesias_Web/Perdon_Web.m4v",
        "perdón" to "LSM_SaludosCortesias_Web/Perdon_Web.m4v",
        "por favor" to "LSM_SaludosCortesias_Web/Porfavor_Web.m4v",
        
        // Personas/Familia
        "mama" to "LSM_Personas_Web/Mama_Web.m4v",
        "mamá" to "LSM_Personas_Web/Mama_Web.m4v",
        "papa" to "LSM_Personas_Web/Papa_Web.m4v",
        "papá" to "LSM_Personas_Web/Papa_Web.m4v",
        "hermano" to "LSM_Personas_Web/Hermano_Web.m4v",
        "hermana" to "LSM_Personas_Web/Hermana_Web.m4v",
        "abuelo" to "LSM_Personas_Web/Abuelo_Web.m4v",
        "abuela" to "LSM_Personas_Web/Abuela_Web.m4v",
        "hijo" to "LSM_Personas_Web/Hijo_Web.m4v",
        "hija" to "LSM_Personas_Web/Hija_Web.m4v"
    )
    
    /**
     * Obtiene la ruta del video para una palabra específica
     * @param word La palabra en español
     * @return La ruta relativa del video o null si no existe
     */
    fun getVideoPath(word: String): String? {
        return videoMap[word.lowercase()]
    }
    
    /**
     * Obtiene el nombre del archivo de video desde assets
     * @param word La palabra en español
     * @return El nombre del archivo en assets/videos/
     */
    fun getAssetVideoPath(word: String): String {
        return when(word.lowercase()) {
            "hola" -> "videos/hola.m4v"
            "adios", "adiós" -> "videos/Adios.m4v"
            "buenos dias", "buenos días" -> "videos/buenos_dias.m4v"
            "buenas noches" -> "videos/buenas_noches.m4v"
            "gracias" -> "videos/Gracias.m4v"
            "mama", "mamá" -> "videos/mama.m4v"
            "papa", "papá" -> "videos/papa.m4v"
            "amarillo" -> "videos/amarillo.m4v"
            "azul" -> "videos/azul.m4v"
            "rojo" -> "videos/rojo.m4v"
            else -> "videos/hola.m4v"
        }
    }
    
    /**
     * Obtiene la ruta completa del video desde la raíz del proyecto
     * @param word La palabra en español
     * @return La ruta completa o una ruta por defecto
     */
    fun getFullVideoPath(word: String): String {
        val relativePath = getVideoPath(word)
        return if (relativePath != null) {
            "Proyecto LSM - Videos e Imágenes AD 2022_5/Proyecto LSM - Videos e Imagenes AD 2022/$relativePath"
        } else {
            "videos/placeholder.m4v" // Ruta por defecto
        }
    }
    
    /**
     * Verifica si existe un video para la palabra
     */
    fun hasVideo(word: String): Boolean {
        return videoMap.containsKey(word.lowercase())
    }
    
    /**
     * Obtiene todas las palabras disponibles para el juego de memoria
     * Retorna pares de palabras que tienen videos en assets
     */
    fun getMemoryGameWords(): List<String> {
        return listOf(
            "Hola",
            "Adiós",
            "Buenos días",
            "Buenas noches",
            "Gracias",
            "Mamá",
            "Papá",
            "Amarillo",
            "Azul",
            "Rojo"
        )
    }
    
    /**
     * Obtiene palabras para quizzes del módulo Abecedario/Saludos
     */
    fun getQuizWords(module: String = "saludos"): List<String> {
        return when(module.lowercase()) {
            "abecedario", "introduccion", "introducción" -> listOf(
                "Hola",
                "Adiós", 
                "Buenos días",
                "Buenas noches",
                "Gracias"
            )
            "colores" -> listOf(
                "Amarillo",
                "Azul",
                "Rojo"
            )
            "familia" -> listOf(
                "Mamá",
                "Papá"
            )
            else -> getMemoryGameWords()
        }
    }
}
