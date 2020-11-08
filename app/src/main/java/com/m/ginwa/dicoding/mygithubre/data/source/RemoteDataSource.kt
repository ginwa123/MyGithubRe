package com.m.ginwa.dicoding.mygithubre.data.source

import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.RetrofitEndPoint
import com.m.ginwa.dicoding.mygithubre.data.model.Followers
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.SearchUser
import com.m.ginwa.dicoding.mygithubre.data.model.User
import retrofit2.Retrofit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val retrofit: Retrofit) {

    suspend fun searchUser(username: String): Result<SearchUser?> = try {
        val task = retrofit.create(RetrofitEndPoint::class.java).searchUser(username)
        Result.Success(task)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getUser(username: String): Result<User?> = try {
        val task = retrofit.create(RetrofitEndPoint::class.java).detailUser(username)
        Result.Success(task)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getFollowersUser(username: String): Result<List<Followers>?> = try {
        val task = retrofit.create(RetrofitEndPoint::class.java).followersUser(username)
        Result.Success(task)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getFollowingsUser(username: String): Result<List<Following>?> = try {
        val task = retrofit.create(RetrofitEndPoint::class.java).followingsUser(username)
        Result.Success(task)
    } catch (e: Exception) {
        Result.Error(e)
    }
}