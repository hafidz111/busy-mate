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
import com.example.busymate.ui.screen.setting.SettingScreen

@Composable
fun BusyMateApp(
    modifier: Modifier = Modifier,
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
                        val userName = navBackStackEntry?.arguments?.getString("userName") ?: ""
                        TopBar(
                            title = userName,
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
                }
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Detail.route && currentRoute != Screen.Login.route) {
                BottomNavigationBar(navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onUMKMClick = { umkm ->
                        navController.navigate(Screen.Detail.createRoute(umkm.id))
                    }
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
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
            ) { backStackEntry ->
                val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: -1
                DetailScreen(umkmId = umkmId)
            }
        }
    }
}