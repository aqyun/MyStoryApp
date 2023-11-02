package com.dicoding.mystoryapp.view.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.api.AddNewStoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<AddNewStoryResponse>()
    val uploadResult: LiveData<AddNewStoryResponse> = _uploadResult

    private var authToken: String = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun uploadData(description: String, imageFilePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = storyRepository.postNewStory(authToken, description, imageFilePath)
                _uploadResult.postValue(response)
            } catch (e: Exception) {
                Log.e("UploadError", "Upload failed", e)
            }
        }
    }
}
