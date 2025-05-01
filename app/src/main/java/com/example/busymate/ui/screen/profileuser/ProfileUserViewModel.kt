package com.example.busymate.ui.screen.profileuser

import androidx.lifecycle.ViewModel
import com.example.busymate.common.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.net.toUri

class ProfileUserViewModel : ViewModel() {
    private val _userData = MutableStateFlow<UiState<FirebaseUser>>(UiState.Loading)
    val userData: StateFlow<UiState<FirebaseUser>> = _userData

    private val user = FirebaseAuth.getInstance().currentUser
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    fun fetchCurrentUser() {
        _userData.value = UiState.Loading
        FirebaseAuth.getInstance().currentUser?.let { user ->
            _userData.value = UiState.Success(user)
        } ?: run {
            _userData.value = UiState.Error("User not found")
        }
    }

    fun updateDisplayName(newName: String, onComplete: () -> Unit) {
        _isUpdating.value = true
        val profileRequest = UserProfileChangeRequest.Builder().setDisplayName(newName).build()

        user?.updateProfile(profileRequest)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                usersRef.child(user.uid).child("name").setValue(newName).addOnCompleteListener {
                    fetchCurrentUser()
                    _isUpdating.value = false
                    onComplete()
                }
            } else {
                _isUpdating.value = false
                onComplete()
            }
        }
    }

    fun updateProfilePhotoUrl(newUrl: String, onComplete: () -> Unit) {
        _isUpdating.value = true
        val profileRequest = UserProfileChangeRequest.Builder().setPhotoUri(newUrl.toUri()).build()

        user?.updateProfile(profileRequest)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                usersRef.child(user.uid).child("photoUrl").setValue(newUrl).addOnCompleteListener {
                    fetchCurrentUser()
                    _isUpdating.value = false
                    onComplete()
                }
            } else {
                _isUpdating.value = false
                onComplete()
            }
        }

    }
}