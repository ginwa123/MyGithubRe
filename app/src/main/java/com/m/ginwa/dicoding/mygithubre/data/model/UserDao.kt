package com.m.ginwa.dicoding.mygithubre.data.model

import androidx.room.*

@Dao
abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE login=:login")
    abstract suspend fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM User WHERE isFavorite=:isFavorite")
    abstract suspend fun getUserFavorite(isFavorite: Boolean = true): List<User>?

    @Query("SELECT * FROM Followers WHERE loginParent=:loginParent")
    abstract suspend fun getFollowersUser(loginParent: String): List<Followers>

    @Query("SELECT * FROM Following WHERE loginParent=:loginParent")
    abstract suspend fun getFollowingsUser(loginParent: String): List<Following>

    @Transaction
    open suspend fun updateFollowers(dataSetFollowers: List<Followers>?, loginParent: String) {
        // check data followers in db
        if (getFollowersUser(loginParent) != dataSetFollowers) {
            // if not same , then update data
            deleteFollowers(loginParent)
            insertFollowers(dataSetFollowers)
        }
        // if same, then do nothing
    }

    @Query("DELETE FROM Followers WHERE loginParent=:loginParent")
    abstract suspend fun deleteFollowers(loginParent: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFollowers(dataSetFollowers: List<Followers>?)

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
}