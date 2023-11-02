package com.dicoding.mystoryapp.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.UserRepository
import com.dicoding.mystoryapp.data.api.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val userRepository: UserRepository): ViewModel() {

    private val signupResult = MutableLiveData<String>()

    fun getSignupResult(): LiveData<String> {
        return signupResult
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                if (!response.error!!) {
                    signupResult.value = "Register Success."
                } else {
                    signupResult.value = response.message ?: "Registration failed."
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message ?: "Registration failed. An error occurred."
                signupResult.value = errorMessage
                Log.e("SignupError", "Registration failed", e)
            }
        }
    }
}
