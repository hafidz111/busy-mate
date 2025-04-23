package com.example.busymate.ui.screen.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.busymate.data.UMKMRepository

class SettingViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    fun logout(context: Context, onLoggedOut: () -> Unit) {
        repository.logout(context)
        onLoggedOut()
    }
}