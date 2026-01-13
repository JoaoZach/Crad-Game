package com.example.unogame.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unogame.ui.theme.game.GameScreen
import com.example.unogame.ui.theme.login.LoginScreen
import com.example.unogame.ui.theme.register.RegisterScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { playerId ->
                    navController.navigate(
                        Screen.Game.passPlayerId(playerId)
                    ) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { playerId ->
                    navController.navigate(
                        Screen.Game.passPlayerId(playerId)
                    ) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("playerId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val playerId =
                backStackEntry.arguments?.getString("playerId")
                    ?: error("playerId missing")

            GameScreen(
                playerId = playerId,
                gameId = ""
            )
        }
    }
}




