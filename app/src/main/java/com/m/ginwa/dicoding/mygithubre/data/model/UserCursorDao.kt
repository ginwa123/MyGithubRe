package com.m.ginwa.dicoding.mygithubre.data.model

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserCursorDao {
    @Query("SELECT * FROM User WHERE isFavorite=:isFavorite")
    fun getUserFavorite(isFavorite: Boolean = true): Cursor
}