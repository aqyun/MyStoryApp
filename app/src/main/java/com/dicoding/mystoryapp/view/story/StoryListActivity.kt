package com.dicoding.mystoryapp.view.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.UserRepository
import com.dicoding.mystoryapp.data.api.ApiConfig
import com.dicoding.mystoryapp.data.api.ListStoryItem
import com.dicoding.mystoryapp.data.pref.UserPreference
import com.dicoding.mystoryapp.data.pref.dataStore
import com.dicoding.mystoryapp.databinding.ActivityStoryListBinding
import com.dicoding.mystoryapp.view.LoadingStateAdapter
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.maps.MapsActivity
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class StoryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryListBinding

    private val userRepository = UserRepository.getInstance(
        UserPreference.getInstance(this.dataStore),
        ApiConfig.getApiService()
    )

    private val storyRepository = StoryRepository(ApiConfig.getApiService())

    private val viewModel: StoryViewModel by viewModels {
        ViewModelFactory(userRepository, storyRepository)
    }

    private val adapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        lifecycleScope.launch {
            userRepository.getSession().collect { user ->
                val authToken = user.token
                viewModel.setAuthToken(authToken)
                viewModel.getStoryList()
                Log.d("AuthToken", authToken)
            }
        }

        viewModel.getStoryList().observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.onItemClickListener = object : StoryAdapter.OnItemClickListener {
            override fun onItemClick(story: ListStoryItem) {
                val intent = Intent(this@StoryListActivity, DetailStoryActivity::class.java)
                intent.putExtra("STORY_ID", story.id)
                startActivity(intent)
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddNewStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStoryList().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                lifecycleScope.launch {
                    val mapsIntent = Intent(this@StoryListActivity, MapsActivity::class.java)
                    startActivity(mapsIntent)
                    finish()
                }
                true
            }
            R.id.action_logout -> {
                lifecycleScope.launch {
                    userRepository.logout()
                    val loginIntent = Intent(this@StoryListActivity, WelcomeActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
