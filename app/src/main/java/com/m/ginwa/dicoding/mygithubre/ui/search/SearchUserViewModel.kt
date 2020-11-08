package com.m.ginwa.dicoding.mygithubre.ui.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.m.ginwa.dicoding.mygithubre.data.Repository
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.SearchUser
import com.m.ginwa.dicoding.mygithubre.data.model.User

class SearchUserViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: Repository
) : ViewModel() {
    var dataSet: List<User>? = null

    fun loadUsers(username: String): LiveData<Result<SearchUser?>> = liveData {
        emit(Result.Loading)
        emit(repository.searchUser(username))
    }
}