package com.m.ginwa.dicoding.mygithubre.ui.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.m.ginwa.dicoding.mygithubre.data.Repository
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.Followers
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.User
import kotlinx.coroutines.delay


class DetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: Repository
) : ViewModel() {

    var dataSetFollowers: List<Followers>? = null
    var dataSetFollowing: List<Following>? = null
    var dataUser: User? = null
    var login: String? = null
    var isLoadUserComplete = false
    var isLoadFollowersComplete: Boolean = false
    var isLoadFollowingComplete: Boolean = false
    val swipeUpListener: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun isHideIndicator(): Boolean {
        if (isLoadFollowersComplete && isLoadFollowingComplete && isLoadUserComplete) {
            return true
        }
        return false
    }

    fun getFollowers(): LiveData<Result<List<Followers>?>> {
        login?.let {
            return liveData {
                // read local first for fast load
                delay(500)
                val getFollowersLocal =
                    repository.getFollowersUser(it, isReadRemote = false, isReadLocal = true)
                when (getFollowersLocal) {
                    is Result.Success -> dataSetFollowers = getFollowersLocal.data
                }
                emit(getFollowersLocal)
                emit(Result.Loading)
                // then read remote for update data
                val getFollowersRemote =
                    repository.getFollowersUser(it, isReadRemote = true, isReadLocal = false)
                when (getFollowersRemote) {
                    is Result.Success -> {
                        getFollowersRemote.data?.forEach { followers -> followers.loginParent = it }
                        dataSetFollowers = getFollowersRemote.data
                        repository.insertFollowers(getFollowersRemote.data, it)
                    }
                }
                emit(getFollowersRemote)
                emit(Result.Complete)
            }
        }
        return liveData { }
    }

    fun getFollowings(): LiveData<Result<List<Following>?>> {
        login?.let { it ->
            return liveData {
                // read local first for fast load
                val getFollowingsLocal =
                    repository.getFollowingsUser(it, isReadRemote = false, isReadLocal = true)
                when (getFollowingsLocal) {
                    is Result.Success -> dataSetFollowing = getFollowingsLocal.data
                }
                emit(getFollowingsLocal)
                emit(Result.Loading)
                // then read remote for update data in db
                val getFollowingRemote =
                    repository.getFollowingsUser(it, isReadRemote = true, isReadLocal = false)
                when (getFollowingRemote) {
                    is Result.Success -> {
                        getFollowingRemote.data?.forEach { following -> following.loginParent = it }
                        dataSetFollowing = getFollowingRemote.data
                        repository.insertFollowings(getFollowingRemote.data, it)
                    }
                }
                emit(getFollowingRemote)
                emit(Result.Complete)
            }
        }
        return liveData { }
    }

    fun getUser(): LiveData<Result<User?>> {
        login?.let {
            return liveData {
                // read local first for fast load
                delay(500)
                var isFavoriteUser = false
                val userLocal = repository.getUser(it, isReadRemote = false, isReadLocal = true)
                when (userLocal) {
                    is Result.Success -> {
                        userLocal.data?.let {
                            isFavoriteUser = it.isFavorite
                        }
                    }
                }
                emit(userLocal)
                emit(Result.Loading)
                // then read remote for update data in db
                val userRemote = repository.getUser(it, isReadRemote = true, isReadLocal = false)
                when (userRemote) {
                    is Result.Success -> {
                        userRemote.data?.let {
                            it.isFavorite = isFavoriteUser
                            dataUser = it
                            repository.insertUser(it)
                        }
                    }
                }
                emit(userRemote)
                emit(Result.Complete)
            }
        }
        return liveData { }
    }
}


