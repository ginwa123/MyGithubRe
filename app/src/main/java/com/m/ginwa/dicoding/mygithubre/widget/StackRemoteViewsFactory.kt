package com.m.ginwa.dicoding.mygithubre.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.data.model.UserDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class StackRemoteViewsFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao
) :
    RemoteViewsService.RemoteViewsFactory {
    private val dataSet = arrayListOf<User>()

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = 0

    override fun onDataSetChanged() {
        runBlocking(Dispatchers.IO) {
            userDao.getUserFavoriteList(true).let {
                val dataSet = arrayListOf<User>()
                dataSet.addAll(it)
                if (this@StackRemoteViewsFactory.dataSet != dataSet) {
                    this@StackRemoteViewsFactory.dataSet.clear()
                    this@StackRemoteViewsFactory.dataSet.addAll(dataSet)
                }
            }
        }
    }

    override fun hasStableIds(): Boolean = false

    override fun getViewAt(position: Int): RemoteViews {
        val view = RemoteViews(context.packageName, R.layout.my_favorite_users_widget_item)
        val intent = Intent()
        if (dataSet.isNotEmpty()) {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(dataSet[position].avatarUrl)
                .submit()
            view.setImageViewBitmap(R.id.imageView, bitmap.get())
            val extras = bundleOf(MyFavoriteUsersWidget.EXTRA_ITEM to dataSet[position].login)
            intent.putExtras(extras)
        }
        view.setOnClickFillInIntent(R.id.imageView, intent)
        return view
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {

    }

}