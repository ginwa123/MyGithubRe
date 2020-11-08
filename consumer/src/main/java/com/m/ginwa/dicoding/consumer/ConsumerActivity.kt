package com.m.ginwa.dicoding.consumer

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_consumer.*

class ConsumerActivity : AppCompatActivity(R.layout.activity_consumer) {
    private lateinit var consumerAdapter: ConsumerAdapter
    private val authority = "com.m.ginwa.dicoding.mygithubre"
    private val scheme = "content"
    private val contentUri: Uri = Uri.Builder().scheme(scheme)
        .authority(authority)
        .appendPath(User.TABLE_NAME)
        .build()
    private val activityViewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        load()
        consumerAdapter = ConsumerAdapter()
        recyclerView.apply {
            adapter = consumerAdapter
            layoutManager = LinearLayoutManager(this@ConsumerActivity)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        consumerAdapter.updateDataSet(activityViewModel.dataSet)
    }

    private fun load(): ArrayList<User>? {
        contentResolver.query(contentUri, null, null, null, null)?.apply {
            val dataSet = arrayListOf<User>()
            while (moveToNext()) {
                val login = getString(getColumnIndex(User.LOGIN))
                val name = getStringOrNull(getColumnIndex(User.NAME))
                val avatarUrl = getStringOrNull(getColumnIndex(User.AVATAR_URL))
                val isFavorite = (getIntOrNull(getColumnIndex(User.IS_FAVORITE)) == 1)
                val company = getStringOrNull(getColumnIndex(User.COMPANY))
                val following = getIntOrNull(getColumnIndex(User.FOLLOWING))
                val followers = getIntOrNull(getColumnIndex(User.FOLLOWERS))
                val location = getStringOrNull(getColumnIndex(User.LOCATION))
                val publicRepo = getIntOrNull(getColumnIndex(User.PUBLIC_REPOS))
                val user = User(
                    avatarUrl = avatarUrl,
                    company = company,
                    followers = followers,
                    following = following,
                    location = location,
                    login = login,
                    name = name,
                    isFavorite = isFavorite,
                    publicRepos = publicRepo
                )
                dataSet.add(user)
                activityViewModel.dataSet = dataSet
            }
            return dataSet

        }
        return null
    }
}
