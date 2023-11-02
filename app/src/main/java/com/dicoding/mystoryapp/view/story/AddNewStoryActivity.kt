package com.dicoding.mystoryapp.view.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.data.UserRepository
import com.dicoding.mystoryapp.data.api.AddNewStoryResponse
import com.dicoding.mystoryapp.data.api.ApiConfig
import com.dicoding.mystoryapp.data.pref.UserPreference
import com.dicoding.mystoryapp.data.pref.dataStore
import com.dicoding.mystoryapp.databinding.ActivityAddNewStoryBinding
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddNewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewStoryBinding

    private var currentImageUri: Uri? = null

    private val viewModel: AddNewStoryViewModel by viewModels {
        ViewModelFactory(
            UserRepository.getInstance(
                UserPreference.getInstance(this.dataStore),
                ApiConfig.getApiService()
            ),
            StoryRepository(ApiConfig.getApiService())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.addDescription.setOnClickListener { addDescription() }
    }

    private fun addDescription() {
        val description = binding.addDescription.text.toString()

        if (description.isNotEmpty()) {
            Toast.makeText(this, "Deskripsi: $description", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.addDescription.text.toString()

            if (description.isNotEmpty()) {
                showLoading(true)

                description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                val userRepository = UserRepository.getInstance(
                    UserPreference.getInstance(this.dataStore),
                    ApiConfig.getApiService()
                )

                lifecycleScope.launch {
                    userRepository.getSession().collect { user ->
                        val authToken = user.token
                        viewModel.setAuthToken(authToken)

                        try {
                            viewModel.uploadData(description, imageFile.path)
                            viewModel.uploadResult.observe(this@AddNewStoryActivity) { response ->
                                showLoading(false)
                                if (!response.error) {
                                    showToast("Cerita berhasil ditambahkan")
                                    refreshStoryList()
                                } else {
                                    showToast("Gagal menambahkan cerita: ${response.message}")
                                }
                            }
                        } catch (e: HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, AddNewStoryResponse::class.java)
                            showToast(errorResponse.message)
                            showLoading(false)
                        }
                    }
                }
            } else {
                showToast("Deskripsi tidak boleh kosong")
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun refreshStoryList() {
        val storyListIntent = Intent(this, StoryListActivity::class.java)
        storyListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(storyListIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
