package com.example.chat_bot.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.chat_bot.data.auth.AuthState
import com.example.chat_bot.screens.*
import com.example.chat_bot.screens.DifficultyLevel
import com.example.chat_bot.ui.components.ObserveSnackbarMessages
import com.example.chat_bot.ui.theme.EnsenasTheme
import com.example.chat_bot.viewmodels.AuthViewModel
import com.example.chat_bot.viewmodels.ViewModelFactory

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Inicio")
    object Modules : BottomNavItem(Screen.Modules.route, Icons.Default.School, "Módulos")
    object Dictionary : BottomNavItem(Screen.Dictionary.route, Icons.Default.MenuBook, "Diccionario")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Perfil")
}

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(context))
    val authState by authViewModel.authState.collectAsState()
    
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomBar by remember { mutableStateOf(false) }
    
    // Observar mensajes de Snackbar
    ObserveSnackbarMessages(snackbarHostState)
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Protección de navegación: redirigir a login si no autenticado
    LaunchedEffect(authState, currentRoute) {
        if (authState is AuthState.Unauthenticated && 
            currentRoute != Screen.Login.route && 
            currentRoute != Screen.Register.route &&
            currentRoute != Screen.Splash.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    // Determinar si mostrar bottom bar
    showBottomBar = when (currentRoute) {
        Screen.Home.route,
        Screen.Modules.route,
        Screen.Dictionary.route,
        Screen.Profile.route -> true
        else -> false
    }
    
    EnsenasTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        val items = listOf(
                            BottomNavItem.Home,
                            BottomNavItem.Modules,
                            BottomNavItem.Dictionary,
                            BottomNavItem.Profile
                        )
                        
                        items.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = navBackStackEntry?.destination?.hierarchy?.any {
                                    it.route == item.route
                                } == true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }
                
                composable(Screen.Login.route) {
                    LoginScreen(
                        onNavigateToRegister = {
                            navController.navigate(Screen.Register.route)
                        },
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }
                
                composable(Screen.Register.route) {
                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.popBackStack()
                        },
                        onRegisterSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    )
                }
                
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToModules = {
                            navController.navigate(Screen.Modules.route)
                        },
                        onNavigateToDictionary = {
                            navController.navigate(Screen.Dictionary.route)
                        },
                        onNavigateToAchievements = {
                            navController.navigate(Screen.Achievements.route)
                        },
                        onNavigateToLeaderboard = {
                            navController.navigate(Screen.Leaderboard.createRoute("weekly"))
                        },
                        onNavigateToChatBot = {
                            navController.navigate(Screen.ChatBot.route)
                        }
                    )
                }
                
                composable(Screen.Modules.route) {
                    ModulesScreen(
                        onModuleClick = { moduleId ->
                            navController.navigate(Screen.Quiz.createRoute(moduleId))
                        }
                    )
                }
                
                composable(Screen.Dictionary.route) {
                    DictionaryScreen(
                        onSignClick = { signWord ->
                            navController.navigate(Screen.DictionaryDetail.createRoute(signWord))
                        }
                    )
                }
                
                composable(
                    route = Screen.DictionaryDetail.route,
                    arguments = listOf(
                        navArgument("signWord") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val signWord = backStackEntry.arguments?.getString("signWord") ?: ""
                    DictionaryDetailScreen(
                        navController = navController,
                        signWord = signWord
                    )
                }
                
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToAchievements = {
                            navController.navigate(Screen.Achievements.route)
                        }
                    )
                }
                
                // Quiz Screen
                composable(
                    route = Screen.Quiz.route,
                    arguments = listOf(
                        navArgument("moduleId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val moduleId = backStackEntry.arguments?.getInt("moduleId") ?: 1
                    QuizScreen(
                        quizId = moduleId,
                        onQuizComplete = { score, xp ->
                            navController.popBackStack()
                        },
                        onExit = {
                            navController.popBackStack()
                        }
                    )
                }
                
                // Memory Game Screen
                composable(
                    route = Screen.MemoryGame.route,
                    arguments = listOf(
                        navArgument("moduleId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val moduleId = backStackEntry.arguments?.getInt("moduleId") ?: 1
                    MemoryGameScreen(
                        difficulty = DifficultyLevel.EASY,
                        onGameComplete = { stars, moves, time ->
                            navController.popBackStack()
                        },
                        onExit = {
                            navController.popBackStack()
                        }
                    )
                }
                
                // ChatBot Screen
                composable(Screen.ChatBot.route) {
                    ChatBotScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                
                // Achievements Screen
                composable(Screen.Achievements.route) {
                    AchievementsScreen(
                        achievements = emptyList(), // TODO: Get from ViewModel
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                
                // Leaderboard Screen
                composable(
                    route = Screen.Leaderboard.route,
                    arguments = listOf(
                        navArgument("type") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type") ?: "weekly"
                    LeaderboardScreen(
                        leaderboardData = null, // TODO: Get from ViewModel
                        currentUserId = "1", // TODO: Get from auth
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onTypeChange = { newType ->
                            navController.navigate(Screen.Leaderboard.createRoute(newType)) {
                                popUpTo(Screen.Leaderboard.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
