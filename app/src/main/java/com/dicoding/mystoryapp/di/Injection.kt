package com.dicoding.mystoryapp.di


import android.content.Context
import com.dicoding.mystoryapp.data.UserRepository
import com.dicoding.mystoryapp.data.api.ApiService
import com.dicoding.mystoryapp.data.pref.UserPreference
import com.dicoding.mystoryapp.data.pref.dataStore
import com.dicoding.mystoryapp.data.StoryRepository

object Injection {
    fun provideUserRepository(context: Context, apiService: ApiService): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(apiService: ApiService): StoryRepository {
        return StoryRepository(apiService)
    }
}
