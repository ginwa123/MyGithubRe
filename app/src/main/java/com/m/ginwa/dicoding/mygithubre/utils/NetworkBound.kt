package com.m.ginwa.dicoding.mygithubre.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.Result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


abstract class NetworkBound<ResultType>(
    private val coroutineScope: CoroutineScope,
    private val dispatcher: Dispatchers
) {
    private val result = MediatorLiveData<Result<ResultType>>()

    init {
        @Suppress("LeakingThis")
        val dbSource = loadFromDB()
        var jobFetch: Job? = null


        result.addSource(dbSource) { data ->
            // load local data first then show completed
            if (data != null) {
                result.value = Success(data)
                result.value = Complete
            }
            result.removeSource(dbSource)
            if (alwaysLoadFromRemote(data)) {
                // TODO: 11/10/20  
//                EspressoIdlingResources.increment()
                result.value = Loading
                jobFetch = coroutineScope.launch(dispatcher.IO) {
                    createCallRemote(data)
                }
                coroutineScope.launch(dispatcher.Main) {
                    jobFetch?.join()
                    result.addSource(dbSource) { newData ->
                        result.value = Success(newData)
                    }
                }
            } else {
                result.addSource(dbSource) { newData ->
                    result.value = Success(newData)
                }
            }
            coroutineScope.launch(dispatcher.Main) {
                jobFetch?.join()
                // TODO: 11/10/20  
//                EspressoIdlingResources.decrement()
                result.value = Complete
            }
        }
    }

    protected open fun onFetchFailed(exception: Exception) {
        coroutineScope.launch(dispatcher.Main) {
            result.value = Error(exception)
        }
    }

    protected abstract fun loadFromDB(): LiveData<ResultType>

    protected abstract fun alwaysLoadFromRemote(data: ResultType?): Boolean

    protected abstract suspend fun createCallRemote(data: ResultType?)

    protected abstract suspend fun insertOnCall(data: Any?)

    fun asLiveData(): LiveData<Result<ResultType>> = result
}