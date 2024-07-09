package com.dicoding.githubuser1.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.githubuser1.database.User
import com.dicoding.githubuser1.database.UserDao
import com.dicoding.githubuser1.database.UserRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun insert(user: User) {
        executorService.execute { mUserDao.insert(user) }
    }

    fun delete(user: User) {
        executorService.execute { mUserDao.delete(user) }
    }

    fun getAllFavoriteUsers(): LiveData<List<User>> {
        return mUserDao.getAllUser()
    }
}