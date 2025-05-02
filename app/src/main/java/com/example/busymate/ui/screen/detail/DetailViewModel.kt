package com.example.busymate.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.ProductItem
import com.example.busymate.model.UMKM
import com.example.busymate.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _umkm = MutableStateFlow<UMKM?>(null)
    val umkm: StateFlow<UMKM?> = _umkm

    private val _productList = MutableStateFlow<List<ProductItem>>(emptyList())
    val productList: StateFlow<List<ProductItem>> = _productList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _ownerUser = MutableStateFlow<UserProfile?>(null)
    val ownerUser: StateFlow<UserProfile?> = _ownerUser

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    private val _followLoading = MutableStateFlow(false)
    val followLoading: StateFlow<Boolean> = _followLoading

    fun getUMKMById(umkmId: String) {
        viewModelScope.launch {
            repository.getUMKMById(umkmId)
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _errorMessage.value = e.message
                    _isLoading.value = false
                }
                .collect { result ->
                    _isLoading.value = false
                    result.onSuccess { data ->
                        _umkm.value = data
                    }.onFailure {
                        _errorMessage.value = it.message
                    }
                }
        }
    }

    fun getProductsByUMKM(umkmId: String) {
        repository.getProducts(umkmId).onEach { result ->
            result.onSuccess { products ->
                _productList.value = products
            }.onFailure {
            }
        }.launchIn(viewModelScope)
    }

    fun fetchOwner(userId: String) = viewModelScope.launch {
        repository.getUserProfile(userId)
            .catch { }
            .collect { res ->
                res.onSuccess { _ownerUser.value = it }
            }
    }

    fun checkFollowing(currentUserId: String, profileUserId: String) = viewModelScope.launch {
        repository.isFollowing(currentUserId, profileUserId)
            .catch { /* log */ }
            .collect { res ->
                res.onSuccess { _isFollowing.value = it }
            }
    }

    fun toggleFollow(profileUserId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _followLoading.value = true
            if (!_isFollowing.value) {
                repository.followUser(currentUserId, profileUserId)
                    .onEach { result ->
                        result.onSuccess { _isFollowing.value = true }
                    }
                    .launchIn(this)
            } else {
                repository.unfollowUser(currentUserId, profileUserId)
                    .onEach { result ->
                        result.onSuccess { _isFollowing.value = false }
                    }
                    .launchIn(this)
            }
            _followLoading.value = false
        }
    }
}
