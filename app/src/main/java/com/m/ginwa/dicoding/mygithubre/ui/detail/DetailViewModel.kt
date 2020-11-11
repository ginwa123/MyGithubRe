package com.m.ginwa.dicoding.mygithubre.ui.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.m.ginwa.dicoding.mygithubre.data.Repository
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.Follower
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.utils.NetworkBound
import kotlinx.coroutines.Dispatchers


class DetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    private val dispatcher: Dispatchers
) : ViewModel() {

    lateinit var followers: LiveData<Result<PagedList<Follower>>>
    lateinit var user: LiveData<Result<User?>>
    lateinit var followings: LiveData<Result<PagedList<Following>>>
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

    @Suppress("UNCHECKED_CAST")
    fun getFollowers() {
        followers = object : NetworkBound<PagedList<Follower>>(viewModelScope, dispatcher) {
            override fun loadFromDB(): LiveData<PagedList<Follower>> {
                val factory = repository.getFollowersLocal(login ?: "")
                return LivePagedListBuilder(factory, 5).build()
            }

            override fun alwaysLoadFromRemote(data: PagedList<Follower>?): Boolean {
                return true
            }

            override suspend fun createCallRemote(data: PagedList<Follower>?) {
                var error: Exception? = null
                when (val resultRemote = repository.getFollowersRemote(login ?: "")) {
                    is Result.Success -> {
                        resultRemote.data?.forEach { it.loginParent = login ?: "" }
                        insertOnCall(resultRemote.data)
                    }
                    is Result.Error -> error = resultRemote.exception
                }
                if (error != null) onFetchFailed(error)
            }

            override suspend fun insertOnCall(data: Any?) {
                repository.insertFollowers(data as List<Follower>?, login ?: "")
            }

        }.asLiveData()
    }

    @Suppress("UNCHECKED_CAST")
    fun getFollowings() {
        followings = object : NetworkBound<PagedList<Following>>(viewModelScope, dispatcher) {
            override fun loadFromDB(): LiveData<PagedList<Following>> {
                val factory = repository.getFollowingLocal(login ?: "")
                return LivePagedListBuilder(factory, 5).build()
            }

            override fun alwaysLoadFromRemote(data: PagedList<Following>?): Boolean {
                return true
            }

            override suspend fun createCallRemote(data: PagedList<Following>?) {
                var error: Exception? = null
                when (val resultRemote = repository.getFollowingRemote(login ?: "")) {
                    is Result.Success -> {
                        resultRemote.data?.forEach { it.loginParent = login ?: "" }
                        insertOnCall(resultRemote.data)
                    }
                    is Result.Error -> error = resultRemote.exception
                }
                if (error != null) onFetchFailed(error)
            }

            override suspend fun insertOnCall(data: Any?) {
                if (data != null && login != null) {
                    repository.insertFollowings(data as List<Following>?, login ?: "")
                }
            }

        }.asLiveData()
    }

    fun getUser() {
        user = object : NetworkBound<User?>(viewModelScope, dispatcher) {
            override fun loadFromDB(): LiveData<User?> {
                return repository.getUserLocal(login ?: "")
            }

            override fun alwaysLoadFromRemote(data: User?): Boolean {
                return true
            }

            override suspend fun createCallRemote(data: User?) {
                var error: Exception? = null
                when (val resultRemote =
                    repository.getUserRemote((data?.login ?: login).toString())) {
                    is Result.Success -> {
                        if (data != null) resultRemote.data?.isFavorite = data.isFavorite
                        insertOnCall(resultRemote.data as Any)
                    }
                    is Result.Error -> error = resultRemote.exception
                }
                if (error != null) onFetchFailed(error)
            }

            override suspend fun insertOnCall(data: Any?) {
                repository.insertUser(data as User)
            }

        }.asLiveData()
    }
}


