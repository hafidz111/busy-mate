package com.example.busymate.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.busymate.data.UMKMRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.busymate.R
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)
    private var loginError by mutableStateOf<String?>(null)

    fun validateInput(context: Context) {
        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            context.getString(R.string.email_not_valid)
        } else null

        passwordError = if (password.length < 8) {
            context.getString(R.string.password_not_valid)
        } else null
    }

    @SuppressLint("UseKtx")
    fun login(context: Context) {
        validateInput(context)

        if (emailError == null && passwordError == null) {
            isLoading = true
            loginError = null

            viewModelScope.launch {
                repository.login(email, password).collect { result ->
                    isLoading = false
                    result.onSuccess {
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("is_logged_in", true).apply()
                        loginSuccess = true
                    }.onFailure {
                        loginError = context.getString(R.string.email_or_password_not_valid)
                    }
                }
            }
        }
    }
}