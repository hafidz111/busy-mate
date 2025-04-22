package com.example.busymate

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.example.busymate.ui.screen.login.LoginScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun WelcomeApp() {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var isLoggedIn by remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                "is_logged_in",
                false
            )
        )
    }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                sharedPreferences.edit {
                    putBoolean("is_logged_in", true)
                }
                isLoggedIn = true
            }
        )
    } else {
        BusyMateApp(
            onLogout = {
                sharedPreferences.edit {
                    putBoolean("is_logged_in", false)
                }
                isLoggedIn = false
            }
        )
    }
}