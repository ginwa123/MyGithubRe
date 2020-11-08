package com.m.ginwa.dicoding.mygithubre.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.data.model.UserCursorDao
import com.m.ginwa.dicoding.mygithubre.di.RoomModule


class MyContentProvider : ContentProvider() {

    private lateinit var db: UserCursorDao
    private val user = 1
    private val authority = "com.m.ginwa.dicoding.mygithubre"

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        matcher.addURI(authority, User.TABLE_NAME, user)
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        context?.let {
            db = RoomModule.room(it).userCursorDao()
        }
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val code = matcher.match(uri)
        Log.d("cursor", "query: $code")
        context?.let { context ->
            return if (code == user) {
                val cursorDao = db.getUserFavorite(true)
                cursorDao.setNotificationUri(context.contentResolver, uri)
                cursorDao
            } else {
                null
            }
        }
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}
