package com.m.ginwa.dicoding.mygithubre.data

import com.m.ginwa.dicoding.mygithubre.data.model.Followers
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.SearchUser
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.di.RetrofitModule.token
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface RetrofitEndPoint {

    @Headers("Authorization: token $token")
    @GET("search/users")
    suspend fun searchUser(@Query("q") username: String): SearchUser

    @Headers("Authorization: token $token")
    @GET("users/{username}")
    suspend fun detailUser(@Path("username") username: String): User

    @Headers("Authorization: token $token")
    @GET("users/{username}/followers")
    suspend fun followersUser(@Path("username") username: String): List<Followers>

    @Headers("Authorization: token $token")
    @GET("users/{username}/following")
    suspend fun followingsUser(@Path("username") username: String): List<Following>


}