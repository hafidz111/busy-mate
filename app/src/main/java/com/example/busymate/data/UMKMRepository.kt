package com.example.busymate.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UMKMRepository(private val firebaseAuth: FirebaseAuth) {
    fun login(email: String, password: String): Flow<Result<FirebaseUser>> = callbackFlow {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.let {
                        trySendBlocking(Result.success(it))
                    } ?: trySendBlocking(Result.failure(Exception("User not found")))
                } else {
                    trySendBlocking(Result.failure(task.exception ?: Exception("Login failed")))
                }
                close()
            }
        awaitClose {}
    }

    @SuppressLint("UseKtx")
    fun logout(context: Context) {
        firebaseAuth.signOut()
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("is_logged_in").apply()
    }
}