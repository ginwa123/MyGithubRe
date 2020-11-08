package com.m.ginwa.dicoding.mygithubre.data.source

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.model.Followers
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.data.model.UserDao
import com.m.ginwa.dicoding.mygithubre.widget.MyFavoriteUsersWidget
import dagger.hilt.android.qualifiers.ApplicationContext
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


    suspend fun getUser(username: String): User? = userDao.getUserByLogin(username)

    suspend fun getUserFavorite(): List<User>? = userDao.getUserFavorite()

    suspend fun getFollowersUser(usernameParent: String): List<Followers> =
        userDao.getFollowersUser(usernameParent)

    suspend fun insertFollowers(dataSetFollowers: List<Followers>?, usernameParent: String) =
        userDao.updateFollowers(dataSetFollowers, usernameParent)

    suspend fun insertFollowings(dataSetFollowing: List<Following>?, usernameParent: String) =
        userDao.updateFollowings(dataSetFollowing, usernameParent)

    suspend fun getFollowingsUser(username: String): List<Following> =
        userDao.getFollowingsUser(username)
}