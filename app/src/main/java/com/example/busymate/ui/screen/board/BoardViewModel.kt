package com.example.busymate.ui.screen.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.Board
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BoardViewModel(
    private val repository: UMKMRepository
) : ViewModel() {
    private val _boardList = MutableStateFlow<List<Board>>(emptyList())
    val boardList: StateFlow<List<Board>> = _boardList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchBoard() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getBoard().collect { result ->
                result.onSuccess { list ->
                    _boardList.value = list
                }.onFailure { exception ->
                    _errorMessage.value = "Gagal memuat Board: ${exception.localizedMessage}"
                }
                _isLoading.value = false
            }
        }
    }
}