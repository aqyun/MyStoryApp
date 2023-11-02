package com.dicoding.mystoryapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.mystoryapp.data.api.AddNewStoryResponse
import com.dicoding.mystoryapp.data.api.ApiService
import com.dicoding.mystoryapp.data.api.DetailStoryResponse
import com.dicoding.mystoryapp.data.api.ListStoryItem
import com.dicoding.mystoryapp.data.api.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(private val apiService: ApiService) {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    suspend fun getDetailStory(token: String, storyId: String): DetailStoryResponse {
        return apiService.getDetailStory("Bearer $token", storyId)
    }

    suspend fun postNewStory(token: String, description: String, imageFilePath: String): AddNewStoryResponse {
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val imageFile = File(imageFilePath)
        val imageFileRequestBody = imageFile.asRequestBody("image/jpeg".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", imageFile.name, imageFileRequestBody)

        return apiService.postNewStory("Bearer $token", photoPart, descriptionRequestBody)
    }

    suspend fun getStoriesWithLocation(token: String, location: Int): StoryResponse {
        return apiService.getStoriesWithLocation("Bearer $token", location)
    }
}
