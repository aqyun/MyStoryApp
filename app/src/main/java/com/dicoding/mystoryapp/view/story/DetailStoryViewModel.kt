package com.dicoding.mystoryapp.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.api.DetailStoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<DetailStoryResponse>()
    val story: LiveData<DetailStoryResponse> = _story

    private var authToken: String = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getDetailStory(storyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = storyRepository.getDetailStory(authToken, storyId)
                _story.postValue(response)
            } catch (e: Exception) {
                Log.e("NetworkError", "Error: ${e.message}")
            }
        }
    }
}