package com.example.busymate.ui.screen.register

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.utils.RegisterInputError
import com.example.busymate.utils.hasError
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UMKMRepository): ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var errorState by mutableStateOf(RegisterInputError())
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    fun onEmailChange(newEmail: String) {
        email = newEmail
        validateInput()
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        validateInput()
    }

    fun onNameChange(newName: String) {
        name = newName
        validateInput()
    }

    private fun validateInput() {
        errorState = RegisterInputError(
            emailError = when {
                email.isBlank() -> "Field tidak boleh kosong"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email tidak valid"
                else -> null
            },
            passwordError = when {
                password.isBlank() -> "Field tidak boleh kosong"
                password.length < 8 -> "Password tidak boleh kurang dari 8 digit"
                else -> null
            },
            nameError = if (name.isBlank()) "Field tidak boleh kosong" else null
        )
    }

    fun register() {
        viewModelScope.launch {
            if (errorState.hasError()) {
                errorMessage = "Mohon memperbaiki input terlebi dahulu"
                return@launch
            }
            repository.register(email, password, name)
                .onEach { result ->
                    result
                        .onSuccess {
                            successMessage = "Register Berhasil"
                        }
                        .onFailure { e ->
                            errorMessage = e.message ?: "Register gagal"
                        }
                }
                .catch { e ->
                    errorMessage = e.message ?: "Terjadi kesalahan"
                }
                .launchIn(this)
        }
    }

    fun clearMessage() {
        successMessage = null
        errorMessage = null
    }

}