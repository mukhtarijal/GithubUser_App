package com.dicoding.githubuser1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuser1.database.User
import com.dicoding.githubuser1.database.UserRoomDatabase
import com.dicoding.githubuser1.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val favoriteUsers: LiveData<List<User>>

    init {
        val db = UserRoomDatabase.getDatabase(application)
        db.userDao()
        userRepository = UserRepository(application)
        favoriteUsers = userRepository.getAllFavoriteUsers()
    }

    fun getFavoriteUsers(): LiveData<List<User>> {
        return favoriteUsers
    }

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insert(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.delete(user)
        }
    }
}
