package com.m.ginwa.dicoding.mygithubre.data

import com.m.ginwa.dicoding.mygithubre.data.model.Followers
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.SearchUser
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.data.source.LocalDataSource
import com.m.ginwa.dicoding.mygithubre.data.source.RemoteDataSource
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    suspend fun searchUser(username: String): Result<SearchUser?> =
        remoteDataSource.searchUser(username)

    suspend fun getFollowersUser(
        username: String,
        isReadRemote: Boolean,
        isReadLocal: Boolean
    ): Result<List<Followers>?> {
        if (isReadLocal) {
            val followers = localDataSource.getFollowersUser(username)
            if (followers.isNotEmpty()) return Result.Success(followers)
        }
        if (isReadRemote) return remoteDataSource.getFollowersUser(username)
        return Result.Success(null)
    }

    suspend fun getFollowingsUser(
        username: String,
        isReadRemote: Boolean,
        isReadLocal: Boolean
    ): Result<List<Following>?> {
        if (isReadLocal) {
            val following = localDataSource.getFollowingsUser(username)
            if (following.isNotEmpty()) return Result.Success(following)
        }
        if (isReadRemote) return remoteDataSource.getFollowingsUser(username)
        return Result.Success(null)
    }

    suspend fun getUser(
        username: String,
        isReadRemote: Boolean,
        isReadLocal: Boolean
    ): Result<User?> {
        if (isReadLocal) {
            val user = localDataSource.getUser(username)
            user?.let { return Result.Success(it) }
        }
        if (isReadRemote) return remoteDataSource.getUser(username)
        return Result.Success(null)
    }

    suspend fun insertUser(user: User) = localDataSource.insertUser(user)

    suspend fun getUserFavorite(): Result.Success<List<User>?> =
        Result.Success(localDataSource.getUserFavorite())

    suspend fun insertFollowers(dataSetFollowers: List<Followers>?, username: String) =
        localDataSource.insertFollowers(dataSetFollowers, username)

    suspend fun insertFollowings(dataSetFollowings: List<Following>?, username: String) {
        localDataSource.insertFollowings(dataSetFollowings, username)
    }


}
