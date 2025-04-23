package com.example.busymate.utils

import androidx.compose.runtime.saveable.Saver

data class RegisterInputError(
    val email: String? = null,
    val password: String? = null,
    val name: String? = null
)

val RegisterInputErrorSaver = Saver<RegisterInputError, List<String?>>(
    save = { listOf(it.email, it.password, it.name) },
    restore = { RegisterInputError(it[0], it[1], it[2]) }
)