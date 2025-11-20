package com.example.chat_bot.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Modules : Screen("modules")
    object Dictionary : Screen("dictionary")
    object DictionaryDetail : Screen("dictionary/{signWord}") {
        fun createRoute(signWord: String) = "dictionary/$signWord"
    }
    object Profile : Screen("profile")
    object Quiz : Screen("quiz/{moduleId}") {
        fun createRoute(moduleId: Int) = "quiz/$moduleId"
    }
    object MemoryGame : Screen("memory/{moduleId}") {
        fun createRoute(moduleId: Int) = "memory/$moduleId"
    }
    object ChatBot : Screen("chatbot")
    object Achievements : Screen("achievements")
    object Leaderboard : Screen("leaderboard/{type}") {
        fun createRoute(type: String) = "leaderboard/$type"
    }
}
