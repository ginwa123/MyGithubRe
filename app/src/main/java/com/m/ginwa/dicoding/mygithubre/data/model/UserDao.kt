package com.m.ginwa.dicoding.mygithubre.data.model

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import java.util.*

@Dao
abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE login=:login")
    abstract fun getUserByLogin(login: String): LiveData<User?>

    @Query("SELECT * FROM User WHERE isFavorite=:isFavorite")
    abstract fun getUserFavorite(isFavorite: Boolean = true): DataSource.Factory<Int, User>

    @Query("SELECT * FROM User WHERE isFavorite=:isFavorite")
    abstract fun getUserFavoriteList(isFavorite: Boolean = true): List<User>

    @Query("SELECT * FROM Follower WHERE loginParent=:loginParent")
    abstract fun getFollowersUser(loginParent: String): DataSource.Factory<Int, Follower>

    @Query("SELECT * FROM Following WHERE loginParent=:loginParent")
    abstract fun getFollowingsUser(loginParent: String): DataSource.Factory<Int, Following>

    @Transaction
    open suspend fun updateFollowers(dataSetFollowers: List<Follower>?, loginParent: String) {
        // check data followers in db
        if (getFollowersUser(loginParent) != dataSetFollowers) {
            // if not same , then update data
            deleteFollowers(loginParent)
            insertFollowers(dataSetFollowers)
        }
        // if same, then do nothing
    }

    @Query("DELETE FROM Follower WHERE loginParent=:loginParent")
    abstract suspend fun deleteFollowers(loginParent: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFollowers(dataSetFollowers: List<Follower>?)

    @Transaction
    open suspend fun updateFollowings(dataSetFollowing: List<Following>?, loginParent: String) {
        // check data following in db
        if (getFollowingsUser(loginParent) != dataSetFollowing) {
            // if not same , then update data
            deleteFollowing(loginParent)
            insertFollowing(dataSetFollowing)
        }
        // if same, then do nothing

    }

    @Query("DELETE FROM Following WHERE loginParent=:loginParent")
    abstract suspend fun deleteFollowing(loginParent: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFollowing(dataSetFollowing: List<Following>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUsers(dataSetUsers: ArrayList<User>)
}