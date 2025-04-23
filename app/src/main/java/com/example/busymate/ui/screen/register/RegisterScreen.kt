package com.example.busymate.ui.screen.register

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busymate.R
import com.example.busymate.ui.component.RegisterField
import com.example.busymate.ui.theme.BusyMateTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var nickname by remember { mutableStateOf("") }
        var errorState by remember { mutableStateOf(RegisterInputError()) }
        val context = LocalContext.current

        val firebaseAuth = FirebaseAuth.getInstance()

        fun validateInput(email: String, password: String, nickname: String): RegisterInputError {
            val emailError = if (email.isBlank()) {
                context.getString(R.string.field_is_blank)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                context.getString(R.string.email_not_valid)
            } else {
                null
            }

            val passwordError = if (password.isBlank()) {
                context.getString(R.string.field_is_blank)
            } else if (password.length < 8) {
                context.getString(R.string.password_not_valid)
            } else {
                null
            }

            val nicknameError = if (nickname.isBlank()) {
                context.getString(R.string.field_is_blank)
            } else {
                null
            }

            return RegisterInputError(emailError, passwordError, nicknameError)
        }

        fun handleRegister() {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    } else {

                    }
                }
        }

        Image(
            painter = painterResource(R.drawable.register),
            contentDescription = "Register Image",
            modifier = modifier
                .padding(4.dp)
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
        )

        Text(
            modifier = modifier
                .padding(4.dp),
            text = "Register",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = modifier
                .padding(4.dp),
            text = "Please Register to Login",
            fontSize = 25.sp
        )

        RegisterField(
            email = email,
            password = password,
            nickname = nickname,
            onEmailChange = {
                email = it
                errorState = validateInput(it, password, nickname)
            },
            onPasswordChange = {
                password = it
                errorState = validateInput(email, it, nickname)
            },
            onNickChange = {
                nickname = it
                errorState = validateInput(email, password, it)
            },
            errorValidation = errorState,
            onRegisterClick = {
                handleRegister()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    BusyMateTheme {
        RegisterScreen()
    }
}