package com.example.busymate.ui.screen.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busymate.R
import com.example.busymate.data.UMKMRepository
import com.example.busymate.ui.ViewModelFactory
import com.example.busymate.ui.component.LoginField
import com.example.busymate.ui.theme.BusyMateTheme
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UseKtx")
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val context = LocalContext.current

    viewModel.apply {
        LaunchedEffect(loginSuccess) {
            if (loginSuccess) onLoginSuccess()
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
                        .verticalScroll(rememberScrollState())
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LoginField(
                        email = email,
                        password = password,
                        emailError = emailError,
                        passwordError = passwordError,
                        onEmailChange = {
                            email = it
                            validateInput(context)
                        },
                        onPasswordChange = {
                            password = it
                            validateInput(context)
                        },
                        onLoginClick = {
                            login(context)
                        },
                        onRegisterClick = onRegisterClick
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
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BusyMateTheme {
        LoginScreen(
            modifier = Modifier.background(Color.Blue),
            onLoginSuccess = {},
            onRegisterClick = {})
    }
}