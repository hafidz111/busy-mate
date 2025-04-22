package com.example.busymate.ui.screen.setting

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Store
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.busymate.R
import com.example.busymate.ui.component.ProfileCard
import com.example.busymate.ui.component.SettingListItem

@Composable
fun SettingScreen(
    onLogout: () -> Unit,
    onProfileUMKM: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        ProfileCard()

        Spacer(modifier = Modifier.height(16.dp))

        SettingListItem(
            icon = Icons.Default.Store,
            title = stringResource(R.string.profile_umkm),
            onClick = onProfileUMKM
        )

        SettingListItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = stringResource(R.string.logout),
            onClick = {
                val sharedPreferences =
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit {
                    remove("is_logged_in")
                }
                onLogout()
            }
        )
    }
}