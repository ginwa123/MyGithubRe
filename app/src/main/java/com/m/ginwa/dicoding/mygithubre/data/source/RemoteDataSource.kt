package com.m.ginwa.dicoding.mygithubre.data.source

import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.RetrofitEndPoint
import com.m.ginwa.dicoding.mygithubre.data.model.Follower
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.SearchUser
import com.m.ginwa.dicoding.mygithubre.data.model.User
import retrofit2.Retrofit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(retrofit: Retrofit) {

    private val service = retrofit.create(RetrofitEndPoint::class.java)

    suspend fun searchUser(username: String): Result<SearchUser?> =
        apiCall { service.searchUser(username) }

    suspend fun getUser(username: String): Result<User?> = apiCall { service.detailUser(username) }

    suspend fun getFollowersUser(username: String): Result<List<Follower>?> =
        apiCall { service.followersUser(username) }

    suspend fun getFollowingsUser(username: String): Result<List<Following>?> =
        apiCall { service.followingsUser(username) }

    private suspend fun <T> apiCall(service: suspend () -> T): Result<T> {
        return try {
            val result = service.invoke()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}