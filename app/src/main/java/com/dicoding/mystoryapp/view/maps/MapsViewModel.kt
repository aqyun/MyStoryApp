package com.dicoding.mystoryapp.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.api.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<StoryResponse>()
    val storiesWithLocation: LiveData<StoryResponse> = _storiesWithLocation

    private var authToken: String = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getStoriesWithLocation(token: String, location: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = storyRepository.getStoriesWithLocation(token, location)
                _storiesWithLocation.postValue(response)
            } catch (e: Exception) {
                Log.e("NetworkError", "Error: ${e.message}")
            }
        }
    }
}