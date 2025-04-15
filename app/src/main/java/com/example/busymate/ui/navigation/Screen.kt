package com.example.busymate.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object Setting : Screen("profile")
    data object Detail : Screen("detail/{umkmId}") {
        fun createRoute(umkmId: Int) = "detail/$umkmId"
    }
}