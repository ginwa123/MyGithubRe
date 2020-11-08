package com.m.ginwa.dicoding.mygithubre.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class SearchUser(
    @SerializedName("items")
    val items: List<User>?
)

@Entity(tableName = User.TABLE_NAME)
data class User(
    @SerializedName("avatar_url")
    @ColumnInfo(name = AVATAR_URL)
    val avatarUrl: String?,
    @SerializedName("company")
    @ColumnInfo(name = COMPANY)
    val company: String?,
    @SerializedName("followers")
    @ColumnInfo(name = FOLLOWERS)
    val followers: Int?,
    @SerializedName("following")
    @ColumnInfo(name = FOLLOWING)
    val following: Int?,
    @SerializedName("location")
    @ColumnInfo(name = LOCATION)
    val location: String?,
    @SerializedName("public_repos")
    @ColumnInfo(name = PUBLIC_REPOS)
    val publicRepos: Int?,
    @SerializedName("login")
    @ColumnInfo(name = LOGIN)
    @PrimaryKey
    val login: String,
    @SerializedName("name")
    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = IS_FAVORITE)
    var isFavorite: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "User"
        const val AVATAR_URL = "avatarUrl"
        const val COMPANY = "company"
        const val NAME = "name"
        const val IS_FAVORITE = "isFavorite"
        const val LOGIN = "login"
        const val LOCATION = "location"
        const val FOLLOWERS = "followers"
        const val FOLLOWING = "followings"
        const val PUBLIC_REPOS = "publicRepos"
    }
}


@Entity
data class Followers(
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("login")
    val login: String,
    var loginParent: String,
    @PrimaryKey(autoGenerate = true)
    val followerId: Long
)

@Entity
data class Following(
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("login")
    val login: String,
    var loginParent: String,
    @PrimaryKey(autoGenerate = true)
    val followingId: Long
)

