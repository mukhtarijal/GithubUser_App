package com.dicoding.githubuser1.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.githubuser1.viewmodel.UserViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser1.R
import com.dicoding.githubuser1.data.adapter.UserAdapter
import com.dicoding.githubuser1.data.retrofit.ApiConfig
import com.dicoding.githubuser1.databinding.ActivityMainBinding
import com.dicoding.githubuser1.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    private val userViewModel: UserViewModel by viewModels {
        val apiService = ApiConfig.getApiServices()
        UserViewModelFactory(apiService, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        userAdapter = UserAdapter()
        binding.rvUser.adapter = userAdapter

        userAdapter.setOnItemClickListener { user ->
            val intent = Intent(this@MainActivity, DetailUserActivity::class.java)
            intent.putExtra("username", user.login)
            startActivity(intent)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    searchView.hide()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            searchView.editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = searchView.text.toString()
                    searchView.hide()
                    searchUser(query)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        observeViewModel()
        if (savedInstanceState == null) {
            searchUser("arif")
        }
    }

    private fun observeViewModel() {
        userViewModel.users.observe(this) { users ->
            userAdapter.submitList(users)
        }

        userViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        userViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showErrorMessage(errorMessage)
            }
        }
    }

    private fun searchUser(query: String) {
        userViewModel.searchUsers(query)
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                goFavoritActivity()
                true
            }

            R.id.action_mode -> {
                goThemeActivity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goThemeActivity() {
        val intent = Intent(this, ThemeActivity::class.java)
        startActivity(intent)
    }

    private fun goFavoritActivity() {
        val intent = Intent(this, UserFavoriteActivity::class.java)
        startActivity(intent)
    }
}