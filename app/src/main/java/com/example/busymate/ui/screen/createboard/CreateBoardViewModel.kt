package com.example.busymate.ui.screen.createboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.Board
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateBoardViewModel (
    private val repository: UMKMRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _created = MutableStateFlow(false)
    val created: StateFlow<Boolean> = _created

    fun createBoard(board: Board) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.createBoard(board)
                .collect { result ->
                    _isLoading.value = false
                    result.onSuccess {
                        _created.value = true
                    }.onFailure { exc ->
                        _errorMessage.value = exc.localizedMessage
                    }
                }
        }
    }
}