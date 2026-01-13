package com.example.unogame.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")
    object Register : Screen("register")

    object Game : Screen("game/{playerId}") {
        fun passPlayerId(playerId: String): String {
            return "game/$playerId"
        }
    }
}




