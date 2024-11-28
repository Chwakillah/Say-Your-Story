package com.app.storyapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.app.storyapp.databinding.ActivityAddStoryBinding
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.repository.StoryRepository
import com.app.storyapp.nonui.utils.UserPreferences
import com.app.storyapp.nonui.utils.dataStore
import com.app.storyapp.nonui.utils.getImageUri
import com.app.storyapp.nonui.viewmodel.AddStoryViewModel
import com.app.storyapp.nonui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var viewModel: AddStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreferences = UserPreferences.getInstance(dataStore)
        val token = runBlocking { userPreferences.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        val repository = StoryRepository.getInstance(apiService, userPreferences)
        viewModel = ViewModelFactory(repository).create(AddStoryViewModel::class.java)

        binding.btnGalleryPhoto.setOnClickListener { startGallery() }
        binding.btnCameraPhoto.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadStory() }

        viewModel.uploadResult.observe(this) { result ->
            result.onSuccess {
                binding.progressBar.visibility = View.GONE
                binding.buttonAdd.isEnabled = true

                Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            }.onFailure {
                binding.progressBar.visibility = View.GONE
                binding.buttonAdd.isEnabled = true

                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonAdd.isEnabled = !isLoading
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
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivStoryPhoto.setImageURI(it)
        }
    }

    private fun uploadStory() {
        if (currentImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.edDescription.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please add a description", Toast.LENGTH_SHORT).show()
            return
        }

        val file = currentImageUri?.let { uriToFile(it, this) }
        file?.let {
            val compressedFile = compressImage(it)

            val requestDescription = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                compressedFile.name,
                requestImageFile
            )
            viewModel.uploadStory(requestDescription, multipartBody)
        }
    }


    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val outputFile = createCustomTempFile(this)
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        outputStream.close()

        return outputFile
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int

        while (inputStream?.read(buffer).also { length = it ?: -1 }!! > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream?.close()
        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(null)
        return File.createTempFile("story_image", ".jpg", storageDir)
    }
}