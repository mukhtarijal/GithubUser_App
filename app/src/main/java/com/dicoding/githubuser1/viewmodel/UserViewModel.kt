package com.dicoding.githubuser1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuser1.data.response.GithubResponse
import com.dicoding.githubuser1.data.response.ItemsItem
import com.dicoding.githubuser1.data.retrofit.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.awaitResponse

class UserViewModel(private val apiService: ApiService) : ViewModel() {
    private val _users = MutableLiveData<List<ItemsItem>>()
    val users: LiveData<List<ItemsItem>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.searchUser(query).awaitResponse()
                handleResponse(response)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }

            _isLoading.value = false
        }
    }

    private fun handleResponse(response: Response<GithubResponse>) {
        if (response.isSuccessful && response.body() != null) {
            val items = response.body()!!.items?.filterNotNull() ?: emptyList()
            if (items.isEmpty()) {
                _errorMessage.value = "No user found"
            } else {
                _users.value = items
            }
        } else {
            _errorMessage.value = "Failed to fetch GitHub users"
        }
    }
}