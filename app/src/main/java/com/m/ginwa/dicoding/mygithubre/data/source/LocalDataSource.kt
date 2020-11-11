package com.m.ginwa.dicoding.mygithubre.data.source

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.model.Follower
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.data.model.UserDao
import com.m.ginwa.dicoding.mygithubre.widget.MyFavoriteUsersWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao
) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
        updateWidgetDataSet()
    }

    private fun updateWidgetDataSet() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MyFavoriteUsersWidget::class.java)
        val ids = appWidgetManager.getAppWidgetIds(componentName)
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.stackView)
    }


    fun getUserByLogin(username: String): LiveData<User?> = userDao.getUserByLogin(username)

    fun getUserFavorite(): DataSource.Factory<Int, User> = userDao.getUserFavorite()

    fun getFollowersUser(usernameParent: String): DataSource.Factory<Int, Follower> =
        userDao.getFollowersUser(usernameParent)

    suspend fun insertFollowers(dataSetFollowers: List<Follower>?, usernameParent: String) =
        userDao.updateFollowers(dataSetFollowers, usernameParent)

    suspend fun insertFollowings(dataSetFollowing: List<Following>?, usernameParent: String) =
        userDao.updateFollowings(dataSetFollowing, usernameParent)

    fun getFollowingsUser(username: String): DataSource.Factory<Int, Following> =
        userDao.getFollowingsUser(username)

    suspend fun insertUsers(dataSetUsers: ArrayList<User>) {
        userDao.insertUsers(dataSetUsers)
    }


}