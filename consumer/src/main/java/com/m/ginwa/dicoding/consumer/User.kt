package com.m.ginwa.dicoding.consumer


data class User(
    val avatarUrl: String?,
    val company: String?,
    val followers: Int?,
    val following: Int?,
    val location: String?,
    val login: String,
    val name: String?,
    var isFavorite: Boolean = false,
    val publicRepos: Int?
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

