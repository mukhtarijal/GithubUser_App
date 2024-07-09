package com.dicoding.githubuser1.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser1.data.adapter.UserAdapter
import com.dicoding.githubuser1.data.response.ItemsItem
import com.dicoding.githubuser1.data.retrofit.ApiConfig
import com.dicoding.githubuser1.databinding.FragmentFollowBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    private var position: Int = 0
    private var username: String = ""
    private lateinit var followAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME) ?: ""
        }
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        followAdapter = UserAdapter()
        binding.rvUser.adapter = followAdapter
        binding.rvUser.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadData() {
        if (position == 1) {
            getFollowers(username)
        } else {
            getFollowing(username)
        }
    }

    private fun getFollowers(username: String) {
        showLoading(true)
        val apiService = ApiConfig.getApiServices()
        apiService.getFollowers(username).enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val followers = response.body()
                    if (followers != null) {
                        followAdapter.submitList(followers)
                    }
                } else {
                    Log.e(TAG, "Failed to fetch followers: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "Failed to fetch followers: ${t.message}")
            }
        })
    }

    private fun getFollowing(username: String) {
        showLoading(true)
        val apiService = ApiConfig.getApiServices()
        apiService.getFollowing(username).enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val following = response.body()
                    if (following != null) {
                        followAdapter.submitList(following)
                    }
                } else {
                    Log.e(TAG, "Failed to fetch following: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "Failed to fetch following: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "com.dicoding.githubuser1.ui.FollowFragment"
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
    }
}