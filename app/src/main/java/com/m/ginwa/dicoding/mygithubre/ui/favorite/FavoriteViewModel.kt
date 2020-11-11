package com.m.ginwa.dicoding.mygithubre.ui.favorite

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.m.ginwa.dicoding.mygithubre.data.Repository
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.utils.NetworkBound
import kotlinx.coroutines.Dispatchers

class FavoriteViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    private val dispatcher: Dispatchers
) : ViewModel() {


    lateinit var dataSet: LiveData<Result<PagedList<User>>>

    @Suppress("UNCHECKED_CAST")
    fun getUserFavorite() {
        dataSet = object : NetworkBound<PagedList<User>>(viewModelScope, dispatcher) {
            override fun loadFromDB(): LiveData<PagedList<User>> {
                val factory = repository.getUserFavorite()
                return LivePagedListBuilder(factory, 5).build()
            }


            override suspend fun createCallRemote(data: PagedList<User>?) {
                var error: Exception? = null
                val list: ArrayList<User> = arrayListOf()
                data?.forEachIndexed { index, user ->
                    when (val resultRemote = repository.getUserRemote(user.login)) {
                        is Result.Success -> {
                            if (data[index] != null) {
                                resultRemote.data?.isFavorite = data[index]?.isFavorite!!
                            }
                            resultRemote.data?.let { list.add(it) }
                        }
                        is Result.Error -> error = resultRemote.exception
                    }
                    error?.let { onFetchFailed(it) }
                }
                insertOnCall(list)
            }

            override fun alwaysLoadFromRemote(data: PagedList<User>?): Boolean {
                return true
            }

            override suspend fun insertOnCall(data: Any?) {
                repository.insertUsers(data as ArrayList<User>)
            }


        }.asLiveData()
    }

}