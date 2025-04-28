package com.example.busymate.utils

data class RegisterInputError(
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null
)

fun RegisterInputError.hasError(): Boolean {
    return emailError != null || passwordError != null || nameError != null
}