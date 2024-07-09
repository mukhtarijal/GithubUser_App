package com.dicoding.githubuser1.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.githubuser1.R
import com.dicoding.githubuser1.data.adapter.SectionsPagerAdapter
import com.dicoding.githubuser1.data.response.DetailUserResponse
import com.dicoding.githubuser1.data.retrofit.ApiConfig
import com.dicoding.githubuser1.database.User
import com.dicoding.githubuser1.databinding.ActivityDetailUserBinding
import com.dicoding.githubuser1.viewmodel.UserFavoriteViewModel
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var username: String
    private lateinit var favoriteUserViewModel: UserFavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        username = intent.getStringExtra("username") ?: ""
        supportActionBar?.title = username

        favoriteUserViewModel = ViewModelProvider(this)[UserFavoriteViewModel::class.java]

        getUserDetail(username)

        val viewPagerAdapter = SectionsPagerAdapter(this)
        viewPagerAdapter.username = username
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Followers"
                1 -> "Following"
                else -> ""
            }
        }.attach()
    }

    private fun getUserDetail(username: String) {
        showLoading(true)
        val apiService = ApiConfig.getApiServices()
        apiService.getUserDetail(username).enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val user = response.body()
                    user?.let {
                        showUserDetail(it)
                        it.login?.let { it1 -> checkUserFavoriteStatus(it1, it.avatarUrl) }
                    }
                } else {
                    val errorMessage = "Failed to fetch user detail: ${response.message()}"
                    binding.tvName.text = errorMessage
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                showLoading(false)
                val errorMessage = "Failed to fetch user detail: ${t.message}"
                binding.tvName.text = errorMessage
            }
        })
    }

    private fun showUserDetail(user: DetailUserResponse) {
        binding.apply {
            tvName.text = user.name ?: "Name not available"
            tvFollower.text = user.followers?.toString() ?: "0"
            tvFollowing.text = user.following?.toString() ?: "0"
            tvRepo.text = user.publicRepos?.toString() ?: "0"
            tvOffice.text = user.company ?: "Not specified"
            tvCountry.text = user.location ?: "Not specified"
            Glide.with(this@DetailUserActivity)
                .load(user.avatarUrl)
                .into(ivAvatar)
        }
    }

    private fun checkUserFavoriteStatus(username: String, avatarUrl: String?) {
        favoriteUserViewModel.getFavoriteUsers().observe(this) { favoriteUsers ->
            val isUserFavorite = favoriteUsers.any { it.username == username }
            setFabIconAndOnClick(isUserFavorite, username, avatarUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        val shareItem = menu?.findItem(R.id.action_share)
        val shareActionProvider =
            shareItem?.let { MenuItemCompat.getActionProvider(it) } as ShareActionProvider
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareText = "Check out this user's profile: ${getUserHtmlUrl()}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        shareActionProvider.setShareIntent(shareIntent)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getUserHtmlUrl(): String {
        val username = intent.getStringExtra("username")
        return "https://github.com/$username"
    }

    private fun setFabIconAndOnClick(
        isUserFavorite: Boolean,
        username: String,
        avatarUrl: String?
    ) {
        binding.fabFav.setImageResource(
            if (isUserFavorite) R.drawable.ic_favorite_on
            else R.drawable.ic_favorite_off
        )

        binding.fabFav.setOnClickListener {
            if (isUserFavorite) {
                val user = User(username, avatarUrl)
                favoriteUserViewModel.deleteUser(user)
            } else {
                val user = User(username, avatarUrl)
                favoriteUserViewModel.insertUser(user)
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}