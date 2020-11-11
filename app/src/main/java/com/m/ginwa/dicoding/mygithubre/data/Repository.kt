package com.m.ginwa.dicoding.mygithubre.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.m.ginwa.dicoding.mygithubre.data.model.Follower
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

//    suspend fun getFollowersUser(
//        username: String,
//        isReadRemote: Boolean,
//        isReadLocal: Boolean
//    ): Result<List<Followers>?> {
//        liveData {
//            emit(Result.Loading)
//            if (isReadLocal) {
//                val followers = localDataSource.getFollowersUser(username)
//                val a = LivePagedListBuilder(followers, 5).build()
//                if (followers.isNotEmpty()) return Result.Success(followers)
//            }
//            emit(Result.Complete)
//        }
//        if (isReadLocal) {
//            val followers = localDataSource.getFollowersUser(username)
//            val a = LivePagedListBuilder(followers, 5).build()
//            if (followers.isNotEmpty()) return Result.Success(followers)
//        }
//        if (isReadRemote) return remoteDataSource.getFollowersUser(username)
//        return Result.Success(null)
//    }

//    suspend fun getFollowingsUser(
//        username: String,
//        isReadRemote: Boolean,
//        isReadLocal: Boolean
//    ): Result<List<Following>?> {
//        if (isReadLocal) {
//            val following = localDataSource.getFollowingsUser(username)
//            if (following.isNotEmpty()) return Result.Success(following)
//        }
//        if (isReadRemote) return remoteDataSource.getFollowingsUser(username)
//        return Result.Success(null)
//    }

//    suspend fun getUser(
//        username: String,
//        isReadRemote: Boolean,
//        isReadLocal: Boolean
//    ): Result<User?> {
//        if (isReadLocal) {
//            val user = localDataSource.getUser(username)
//            user?.let { return Result.Success(it) }
//        }
//        if (isReadRemote) return remoteDataSource.getUser(username)
//        return Result.Success(null)
//    }

    fun getFollowingLocal(username: String): DataSource.Factory<Int, Following> =
        localDataSource.getFollowingsUser(username)

    fun getUserLocal(username: String): LiveData<User?> = localDataSource.getUserByLogin(username)

    suspend fun getUserRemote(username: String): Result<User?> = remoteDataSource.getUser(username)

    suspend fun insertUser(user: User) = localDataSource.insertUser(user)

    fun getUserFavorite(): DataSource.Factory<Int, User> =
        localDataSource.getUserFavorite()

    suspend fun insertFollowers(dataSetFollowers: List<Follower>?, username: String) =
        localDataSource.insertFollowers(dataSetFollowers, username)

    suspend fun insertFollowings(dataSetFollowings: List<Following>?, username: String) {
        localDataSource.insertFollowings(dataSetFollowings, username)
    }


    suspend fun getFollowingRemote(username: String): Result<List<Following>?> =
        remoteDataSource.getFollowingsUser(username)

    fun getFollowersLocal(usernameParent: String): DataSource.Factory<Int, Follower> =
        localDataSource.getFollowersUser(usernameParent)

    suspend fun getFollowersRemote(username: String): Result<List<Follower>?> =
        remoteDataSource.getFollowersUser(username)

    suspend fun insertUsers(dataSetUsers: ArrayList<User>) {
        localDataSource.insertUsers(dataSetUsers)
    }


}
