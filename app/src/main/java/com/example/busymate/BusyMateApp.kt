package com.example.busymate

import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.busymate.ui.component.BottomNavigationBar
import com.example.busymate.ui.component.TopBar
import com.example.busymate.ui.navigation.Screen
import com.example.busymate.ui.screen.board.BoardScreen
import com.example.busymate.ui.screen.detail.DetailScreen
import com.example.busymate.ui.screen.home.HomeScreen
import com.example.busymate.ui.screen.login.LoginScreen
import com.example.busymate.ui.screen.profile.ProfileUMKMScreen
import com.example.busymate.ui.screen.profile.CreateUMKMScreen
import com.example.busymate.ui.screen.profile.EditUMKMScreen
import com.example.busymate.ui.screen.register.RegisterScreen
import com.example.busymate.ui.screen.setting.SettingScreen

@Composable
fun BusyMateApp(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                when (currentRoute) {
                    Screen.Home.route -> {
                        TopBar(
                            title = stringResource(R.string.app_name),
                            showBackButton = false
                        )
                    }

                    Screen.Board.route -> {
                        TopBar(
                            title = stringResource(R.string.menu_board),
                            navController = navController,
                            showBackButton = false
                        )
                    }

                    Screen.Detail.route -> {
                        val nameUMKM = navBackStackEntry?.arguments?.getString("nameUMKM") ?: ""
                        TopBar(
                            title = nameUMKM,
                            navController = navController,
                            showBackButton = true
                        )
                    }

                    Screen.Setting.route -> {
                        TopBar(
                            title = stringResource(R.string.menu_setting),
                            navController = navController,
                            showBackButton = false
                        )
                    }

                    Screen.ProfileUMKM.route -> {
                        TopBar(
                            title = stringResource(R.string.profile_umkm),
                            navController = navController,
                            showBackButton = true
                        )
                    }

                    Screen.CreateUMKM.route -> {
                        TopBar(
                            title = stringResource(R.string.create_umkm),
                            navController = navController,
                            showBackButton = true
                        )
                    }

                    Screen.EditUMKM.route -> {
                        TopBar(
                            title = stringResource(R.string.edit_umkm),
                            navController = navController,
                            showBackButton = true
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute == Screen.Home.route || currentRoute == Screen.Board.route || currentRoute == Screen.Setting.route) {
                BottomNavigationBar(navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(Screen.Board.route) {
                BoardScreen(

                )
            }
            composable(Screen.Setting.route) {
                SettingScreen(
                    onLogout = {
                        onLogout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onProfileUMKM = {
                        navController.navigate("profile_umkm")
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("umkmId") { type = NavType.StringType },
                    navArgument("nameUMKM") { type = NavType.StringType }
                )
            ) { backStack ->
                val id = backStack.arguments?.getString("umkmId") ?: return@composable
                val nameUMKM = backStack.arguments?.getString("nameUMKM") ?: ""
                DetailScreen(umkmId = id, nameUMKM = nameUMKM)
            }
            composable(Screen.ProfileUMKM.route) {
                ProfileUMKMScreen(
                    navController = navController
                )
            }
            composable(Screen.CreateUMKM.route) {
                CreateUMKMScreen(
                    onCreateSuccess = { navController.popBackStack() },
                    modifier = Modifier
                )
            }
            composable(
                route = Screen.EditUMKM.route,
                arguments = listOf(navArgument("umkmId") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("umkmId") ?: return@composable
                EditUMKMScreen(
                    umkmId = id,
                    navController = navController
                )
            }
        }
    }
}