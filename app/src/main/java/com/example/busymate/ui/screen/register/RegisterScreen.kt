package com.example.busymate.ui.screen.register

import android.util.Patterns
import android.widget.Toast
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier, onRegisterSuccess: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var errorState by remember { mutableStateOf(RegisterInputError()) }
        val context = LocalContext.current

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance().reference

        fun validateInput(email: String, password: String, name: String): RegisterInputError {
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

            val nameError = if (name.isBlank()) {
                context.getString(R.string.field_is_blank)
            } else {
                null
            }

            return RegisterInputError(emailError, passwordError, nameError)
        }

        fun handleRegister() {
            val validation = validateInput(email, password, name)
            errorState = validation

            if (validation.email == null && validation.password == null && validation.name == null) {

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            val uid = user?.uid.orEmpty()

                            val userData = mapOf(
                                "email" to email, "name" to name
                            )
                            firebaseDatabase.child("users").child(uid).setValue(userData)

                            val profileUpdates =
                                UserProfileChangeRequest.Builder().setDisplayName(name).build()

                            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    FirebaseAuth.getInstance().currentUser?.reload()
                                        ?.addOnCompleteListener {
                                            Toast.makeText(
                                                context, "Register Berhasil", Toast.LENGTH_SHORT
                                            ).show()
                                            onRegisterSuccess()
                                        }
                                }
                            }?.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Gagal Simpan name: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Register Gagal: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
            modifier = modifier.padding(4.dp),
            text = "Register",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = modifier.padding(4.dp), text = "Please Register to Login", fontSize = 25.sp
        )

        RegisterField(email = email, password = password, name = name, onEmailChange = {
            email = it
            errorState = validateInput(it, password, name)
        }, onPasswordChange = {
            password = it
            errorState = validateInput(email, it, name)
        }, onNickChange = {
            name = it
            errorState = validateInput(email, password, it)
        }, errorValidation = errorState, onRegisterClick = {
            handleRegister()
        })
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    BusyMateTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}