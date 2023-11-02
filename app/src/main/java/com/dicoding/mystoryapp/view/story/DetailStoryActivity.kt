package com.dicoding.mystoryapp.view.story

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.UserRepository
import com.dicoding.mystoryapp.data.api.ApiConfig
import com.dicoding.mystoryapp.data.pref.UserPreference
import com.dicoding.mystoryapp.data.pref.dataStore
import com.dicoding.mystoryapp.databinding.ActivityDetailStoryBinding
import com.dicoding.mystoryapp.view.ViewModelFactory
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID")

        val userRepository = UserRepository.getInstance(
            UserPreference.getInstance(this.dataStore),
            ApiConfig.getApiService()
        )

        val storyRepository = StoryRepository(ApiConfig.getApiService())

        val viewModel: DetailStoryViewModel by viewModels {
            ViewModelFactory(userRepository, storyRepository)
        }

        showLoading(true)

        lifecycleScope.launch {
            userRepository.getSession().collect { user ->
                val authToken = user.token
                viewModel.setAuthToken(authToken)
                if (storyId != null) {
                    viewModel.getDetailStory(storyId)
                }
            }
        }

        viewModel.story.observe(this) { response ->
            showLoading(false)
            if (!response.error) {
                displayStoryDetail(response.story.name, response.story.description, response.story.photoUrl)
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayStoryDetail(name: String, description: String, photoUrl: String) {
        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description
        Glide.with(this).load(photoUrl).into(binding.ivDetailPhoto)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
