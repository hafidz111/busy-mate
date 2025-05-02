package com.example.busymate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busymate.data.UMKMRepository
import com.example.busymate.ui.screen.board.BoardViewModel
import com.example.busymate.ui.screen.createboard.CreateBoardViewModel
import com.example.busymate.ui.screen.createumkm.CreateUMKMViewModel
import com.example.busymate.ui.screen.detail.DetailViewModel
import com.example.busymate.ui.screen.editumkm.EditUMKMViewModel
import com.example.busymate.ui.screen.home.HomeViewModel
import com.example.busymate.ui.screen.login.LoginViewModel
import com.example.busymate.ui.screen.manageproduct.ManageProductViewModel
import com.example.busymate.ui.screen.profileumkm.ProfileUMKMViewModel
import com.example.busymate.ui.screen.profileuser.ProfileUserViewModel
import com.example.busymate.ui.screen.register.RegisterViewModel

class ViewModelFactory(private val repository: UMKMRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ProfileUMKMViewModel::class.java)) {
            return ProfileUMKMViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(CreateUMKMViewModel::class.java)) {
            return CreateUMKMViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(EditUMKMViewModel::class.java)) {
            return EditUMKMViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ProfileUserViewModel::class.java)) {
            return ProfileUserViewModel() as T
        } else if (modelClass.isAssignableFrom(BoardViewModel::class.java)) {
            return BoardViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(CreateBoardViewModel::class.java)) {
            return CreateBoardViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ManageProductViewModel::class.java)) {
            return ManageProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}