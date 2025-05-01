package com.example.busymate.ui.screen.editumkm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.UMKM
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditUMKMViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateResult = MutableSharedFlow<Result<Unit>>()
    val updateResult: SharedFlow<Result<Unit>> = _updateResult

    fun updateUMKM(umkm: UMKM) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateUMKM(umkm).collect { result ->
                _isLoading.value = false
                _updateResult.emit(result)
            }
        }
    }
}