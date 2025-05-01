package com.example.busymate.ui.screen.profileumkm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.ProductItem
import com.example.busymate.model.UMKM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileUMKMViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _umkmData = MutableStateFlow<UMKM?>(null)
    val umkmData: StateFlow<UMKM?> = _umkmData

    private val _productList = MutableStateFlow<List<ProductItem>>(emptyList())
    val productList: StateFlow<List<ProductItem>> = _productList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchUMKM(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUMKMByUserId(userId).collect { result ->
                result.onSuccess { umkm ->
                    _umkmData.value = umkm
                }.onFailure {
                    _umkmData.value = null
                }

                _isLoading.value = false
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
}