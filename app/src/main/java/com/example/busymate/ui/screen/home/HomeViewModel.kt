package com.example.busymate.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.Category
import com.example.busymate.model.UMKM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _umkmList = MutableStateFlow<List<UMKM>>(emptyList())
    val umkmList: StateFlow<List<UMKM>> = _umkmList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    fun fetchCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { result ->
                result.onSuccess { list ->
                    val allCategory = Category(0, "Semua")
                    _categories.value = listOf(allCategory) + list

                    _selectedCategory.value = allCategory
                }.onFailure { exception ->
                    _errorMessage.value = "Gagal memuat kategori: ${exception.localizedMessage}"
                }
            }
        }
    }

    fun fetchUMKM() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUMKM().collect { result ->
                result.onSuccess { list ->
                    _umkmList.value = list
                }.onFailure { exception ->
                    _errorMessage.value = "Gagal memuat UMKM: ${exception.localizedMessage}"
                }
                _isLoading.value = false
            }
        }
    }
}