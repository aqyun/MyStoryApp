package com.dicoding.mystoryapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.api.ListStoryItem

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private var authToken: String = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getStoryList(): LiveData<PagingData<ListStoryItem>> {
        return repository.getStories("Bearer $authToken").cachedIn(viewModelScope)
    }
}
