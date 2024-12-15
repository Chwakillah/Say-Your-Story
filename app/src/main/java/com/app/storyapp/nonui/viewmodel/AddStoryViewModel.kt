package com.app.storyapp.nonui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.StoryResponse
import com.app.storyapp.nonui.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<Result<StoryResponse>>()
    val uploadResult: LiveData<Result<StoryResponse>> = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadStory(description: RequestBody, photo: MultipartBody.Part) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.uploadStory(description, photo)
                _uploadResult.value = Result.success(result)
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}