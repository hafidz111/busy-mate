package com.example.busymate.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Board : Screen("board")
    data object Setting : Screen("profile")
    data object ProfileUMKM : Screen("profile_umkm")
    data object Detail : Screen("detail/{umkmId}")
}