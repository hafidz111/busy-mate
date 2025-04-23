package com.example.busymate.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.UMKM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _umkm = MutableStateFlow<UMKM?>(null)
    val umkm: StateFlow<UMKM?> = _umkm

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
}