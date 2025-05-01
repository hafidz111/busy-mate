package com.example.busymate.ui.screen.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busymate.data.UMKMRepository
import com.example.busymate.model.Board
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BoardViewModel(
    private val repository: UMKMRepository,
) : ViewModel() {
    private val _boardList = MutableStateFlow<List<Board>>(emptyList())
    val boardList: StateFlow<List<Board>> = _boardList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchBoard(currentUserId: String) {
        viewModelScope.launch {
            combine(
                repository.getBoard(),
                repository.getFollowingList(currentUserId)
            ) { boardRes, followRes ->
                boardRes to followRes
            }.collect { (boardRes, followRes) ->
                _isLoading.value = false

                boardRes.onFailure {
                    _errorMessage.value = it.message
                    return@collect
                }

                val allBoards = boardRes.getOrNull() ?: emptyList()
                val following = followRes.getOrDefault(emptyList())

                val filtered = allBoards.filter { board ->
                    when {
                        board.umkm.id == currentUserId -> true
                        !board.isPrivate && following.contains(board.umkm.id) -> true
                        else -> false
                    }
                }
                _boardList.value = filtered
            }
        }
    }

    fun deleteBoard(boardId: String) = viewModelScope.launch {
        repository.deleteBoard(boardId)
            .collect { result ->
                result
                    .onSuccess {
                        _boardList.value = _boardList.value.filterNot { it.id == boardId }
                    }
                    .onFailure {
                        _errorMessage.value = it.message
                    }
            }
    }
}