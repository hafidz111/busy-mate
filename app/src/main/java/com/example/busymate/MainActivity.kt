package com.example.busymate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.busymate.ui.theme.BusyMateTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    @SuppressLint("UseKtx")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val initiallyLoggedIn = prefs.getBoolean("is_logged_in", false)
        setContent {
            var isLoggedIn by rememberSaveable { mutableStateOf(initiallyLoggedIn) }

            BusyMateTheme {
                BusyMateApp(
                    isLoggedIn = isLoggedIn,
                    onLogout = {
                        isLoggedIn = false
                    }
                )
            }
        }
    }
}