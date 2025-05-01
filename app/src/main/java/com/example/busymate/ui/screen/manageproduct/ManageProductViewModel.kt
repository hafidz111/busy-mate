package com.example.busymate.ui.screen.manageproduct

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.common.UiState
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.ProductItem
import com.example.busymate.utils.uploadImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManageProductViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _products = MutableStateFlow<UiState<List<ProductItem>>>(UiState.Loading)
    val products: StateFlow<UiState<List<ProductItem>>> = _products

    private val _hasStore = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val hasStore: StateFlow<UiState<Boolean>> = _hasStore

    fun checkHasStore(userId: String) {
        viewModelScope.launch {
            repository.hasUMKM(userId).collect { result ->
                result
                    .onSuccess {
                        _hasStore.value = UiState.Success(it)
                    }.onFailure {
                        _hasStore.value = UiState.Error(it.localizedMessage ?: "Error")
                    }
            }
        }
    }

    fun fetchProducts(userId: String) {
        _products.value = UiState.Loading
        viewModelScope.launch {
            repository.getProducts(userId).collect { result ->
                result.onSuccess {
                    _products.value = UiState.Success(it)
                }.onFailure {
                    _products.value = UiState.Error(it.message ?: "Gagal memuat produk")
                }
            }
        }
    }

    fun addProduct(context: Context, userId: String, product: ProductItem, imageUri: Uri?) {
        viewModelScope.launch {
            _products.value = UiState.Loading
            val imageUrl = imageUri?.let { uploadImage(it, context) } ?: ""
            val productWithImage = product.copy(imageUrl = imageUrl)
            repository.addProduct(userId, productWithImage).collect {
                fetchProducts(userId)
            }
        }
    }

    fun editProduct(userId: String, product: ProductItem) {
        viewModelScope.launch {
            _products.value = UiState.Loading
            repository.updateProduct(userId, product).collect {
                fetchProducts(userId)
            }
        }
    }

    fun deleteProduct(userId: String, productId: String) {
        viewModelScope.launch {
            _products.value = UiState.Loading
            repository.deleteProduct(userId, productId).collect {
                fetchProducts(userId)
            }
        }
    }
}