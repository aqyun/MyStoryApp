package com.dicoding.mystoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.UserRepository
import com.dicoding.mystoryapp.data.api.ApiService
import com.dicoding.mystoryapp.di.Injection
import com.dicoding.mystoryapp.view.login.LoginViewModel
import com.dicoding.mystoryapp.view.main.MainViewModel
import com.dicoding.mystoryapp.view.maps.MapsViewModel
import com.dicoding.mystoryapp.view.signup.SignupViewModel
import com.dicoding.mystoryapp.view.story.AddNewStoryViewModel
import com.dicoding.mystoryapp.view.story.DetailStoryViewModel
import com.dicoding.mystoryapp.view.story.StoryViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailStoryViewModel::class.java) -> {
                DetailStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AddNewStoryViewModel::class.java) -> {
                AddNewStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context, apiService: ApiService): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideUserRepository(context, apiService),
                        Injection.provideStoryRepository(apiService)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
