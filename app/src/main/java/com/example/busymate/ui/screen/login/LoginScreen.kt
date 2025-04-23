package com.example.busymate.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busymate.R
import com.example.busymate.ui.component.LoginField
import com.example.busymate.ui.theme.BusyMateTheme
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UseKtx")
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val firebaseAuth = FirebaseAuth.getInstance()

    fun validateInput(emailInput: String, passwordInput: String) {
        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            context.getString(R.string.email_not_valid)
        } else null

        passwordError = if (passwordInput.length < 8) {
            context.getString(R.string.password_not_valid)
        } else null
    }

    fun handleLogin() {
        validateInput(email, password)

        if (emailError == null && passwordError == null) {
            isLoading = true
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        val sharedPreferences =
                            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
                        onLoginSuccess()
                    } else {
                        passwordError = context.getString(R.string.email_or_password_not_valid)
                    }
                }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = modifier
                .padding(top = 80.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            elevation = CardDefaults.elevatedCardElevation(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {

                Image(
                    painter = painterResource(R.drawable.login),
                    contentDescription = "Login Image",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(250.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 12.dp),
                    text = stringResource(R.string.lets_get_started),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                LoginField(
                    email = email,
                    password = password,
                    emailError = emailError,
                    passwordError = passwordError,
                    onEmailChange = {
                        email = it
                        validateInput(it, password)
                    },
                    onPasswordChange = {
                        password = it
                        validateInput(email, it)
                    },
                    onLoginClick = {
                        handleLogin()
                    },
                    onRegisterClick = {}
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BusyMateTheme {
        LoginScreen(modifier = Modifier.background(Color.Blue), onLoginSuccess = {})
    }
}