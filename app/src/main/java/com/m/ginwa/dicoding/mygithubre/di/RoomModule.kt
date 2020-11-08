package com.m.ginwa.dicoding.mygithubre.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.m.ginwa.dicoding.mygithubre.data.model.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RoomModule {

    private var db: GithubDatabase? = null

    @Singleton
    @Provides
    fun room(@ApplicationContext context: Context): GithubDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                GithubDatabase::class.java,
                "my_github_re_db"
            ).build()
        }
        return db as GithubDatabase
    }

    @Singleton
    @Provides
    fun provideUserDao(githubDatabase: GithubDatabase): UserDao = githubDatabase.userDao()
}

@Database(
    entities = [User::class, Following::class, Followers::class],
    version = 1,
    exportSchema = false
)
abstract class GithubDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userCursorDao(): UserCursorDao
}