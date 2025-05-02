package com.example.busymate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.busymate.ui.theme.BusyMateTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.busymate.data.UMKMRepository
import com.example.busymate.ui.screen.setting.SettingPreferences
import com.example.busymate.ui.screen.setting.SettingViewModel
import com.example.busymate.ui.screen.setting.SettingViewModelFactory
import com.example.busymate.ui.screen.setting.dataStore
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    @SuppressLint("UseKtx")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val initiallyLoggedIn = prefs.getBoolean("is_logged_in", false)

        val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()), settingPreferences)
        )[SettingViewModel::class.java]

        setContent {
            val isDarkMode by settingViewModel.isDarkMode.collectAsState()
            var isLoggedIn by rememberSaveable { mutableStateOf(initiallyLoggedIn) }

            BusyMateTheme(darkTheme = isDarkMode) {
                BusyMateApp(
                    isLoggedIn = isLoggedIn,
                    onLogout = {
                        isLoggedIn = false
                    },
                    settingViewModel = settingViewModel
                )
            }
        }
    }
}