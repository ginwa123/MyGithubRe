package com.m.ginwa.dicoding.mygithubre.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m.ginwa.dicoding.mygithubre.data.Repository
import com.m.ginwa.dicoding.mygithubre.data.model.User
import kotlinx.coroutines.launch

class ActivityViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    var startOnce: Boolean = true
    val fabIconListener: MutableLiveData<User> by lazy { MutableLiveData<User>(null) }
    val toolbarListener: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(null) }
    val imageToolbarListener: MutableLiveData<String> by lazy { MutableLiveData<String>(null) }
    val progressBarLive: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(null) }

    fun insertUser(user: User?) {
        viewModelScope.launch {
            user?.let {
                repository.insertUser(it)
            }
        }
    }
}
