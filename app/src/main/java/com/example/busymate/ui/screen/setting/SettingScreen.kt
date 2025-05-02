package com.example.busymate.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.busymate.R
import com.example.busymate.ui.component.ProfileCard
import com.example.busymate.ui.component.SettingListItem
import com.example.busymate.ui.component.SettingListItemWithSwitch
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onProfileUMKM: () -> Unit,
    viewModel: SettingViewModel
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ProfileCard(navController = navController)

        Spacer(modifier = Modifier.height(16.dp))

        SettingListItem(
            icon = Icons.Default.Store,
            title = stringResource(R.string.profile_umkm),
            onClick = onProfileUMKM
        )

        SettingListItem(
            icon = Icons.Default.Shop,
            title = stringResource(R.string.manage_products),
            onClick = {
                FirebaseAuth.getInstance().currentUser?.uid?.let {
                    navController.navigate("manage_product/$it")
                }
            }
        )

        HorizontalDivider(thickness = 2.dp)

        SettingListItemWithSwitch(
            icon = Icons.Default.DarkMode,
            title = stringResource(R.string.dark_mode),
            isChecked = isDarkMode,
            onCheckedChange = { viewModel.saveThemeSetting(it) }
        )

        HorizontalDivider(thickness = 2.dp)

        SettingListItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = stringResource(R.string.logout),
            onClick = { viewModel.logout(context, onLogout) }
        )
    }
}