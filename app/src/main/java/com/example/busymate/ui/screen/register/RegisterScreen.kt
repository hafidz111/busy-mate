package com.example.busymate.ui.screen.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.busymate.ui.component.RegisterField
import com.example.busymate.ui.theme.BusyMateTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier, onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = ViewModelFactory(UMKMRepository(FirebaseAuth.getInstance()))
    )
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        viewModel.apply {
            LaunchedEffect(successMessage, errorMessage) {
                successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    onRegisterSuccess()
                    viewModel.clearMessage()
                }
                errorMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessage()
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
                modifier = modifier.padding(start = 12.dp, bottom = 4.dp),
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = modifier.padding(start = 12.dp),
                text = stringResource(R.string.please_register_to_login),
                fontSize = 24.sp
            )

            RegisterField(
                email = email, password = password, name = name,
                onEmailChange = {
                    viewModel.onEmailChange(it)
                }, onPasswordChange = {
                    viewModel.onPasswordChange(it)
                }, onNickChange = {
                    viewModel.onNameChange(it)
                }, errorValidation = errorState,
                onRegisterClick = {
                    viewModel.register()
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    BusyMateTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}