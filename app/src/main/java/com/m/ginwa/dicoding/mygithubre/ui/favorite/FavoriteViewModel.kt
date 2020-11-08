package com.m.ginwa.dicoding.mygithubre.ui.favorite

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m.ginwa.dicoding.mygithubre.data.Repository
import com.m.ginwa.dicoding.mygithubre.data.model.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class FavoriteViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: Repository
) : ViewModel() {

    var dataSet: List<User>? = null

    suspend fun getUserFavoriteAsync(): Deferred<List<User>?> {
        return viewModelScope.async(Dispatchers.IO) {
            repository.getUserFavorite().data
        }
    }

}