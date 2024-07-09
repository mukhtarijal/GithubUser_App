package com.dicoding.githubuser1.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser1.data.adapter.UserAdapter
import com.dicoding.githubuser1.data.response.ItemsItem
import com.dicoding.githubuser1.databinding.ActivityUserFavoriteBinding
import com.dicoding.githubuser1.viewmodel.UserFavoriteViewModel

class UserFavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserFavoriteBinding
    private lateinit var userFavoriteViewModel: UserFavoriteViewModel
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Favorite Users"
        binding = ActivityUserFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserAdapter()
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = userAdapter

        userFavoriteViewModel = ViewModelProvider(this)[UserFavoriteViewModel::class.java]

        userFavoriteViewModel.getFavoriteUsers().observe(this) { favoriteUsers ->
            val items = favoriteUsers.map { user ->
                ItemsItem(login = user.username, avatarUrl = user.avatarUrl)
            }
            userAdapter.submitList(items)
        }
        userAdapter.setOnItemClickListener { user ->
            val intent = Intent(this, DetailUserActivity::class.java)
            intent.putExtra("username", user.login)
            startActivity(intent)
        }
    }
}
