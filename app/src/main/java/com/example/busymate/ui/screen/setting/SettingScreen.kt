package com.example.busymate.ui.screen.setting

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busymate.R
import com.example.busymate.data.UMKMRepository
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.ProfileCard
import com.example.busymate.ui.component.SettingListItem
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(
    onLogout: () -> Unit,
    onProfileUMKM: () -> Unit,
    viewModel: SettingViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
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
            onClick = { viewModel.logout(context, onLogout) }
        )
    }
}