package com.example.busymate.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Board : Screen("board")
    data object Setting : Screen("profile")
    data object Detail : Screen("detail/{umkmId}/{nameUMKM}")
    data object ProfileUMKM : Screen("profile_umkm")
    data object CreateUMKM : Screen("create_umkm")
    data object EditUMKM : Screen("edit_umkm/{umkmId}")
}