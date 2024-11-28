package com.app.storyapp.nonui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.ListStoryItem
import com.app.storyapp.nonui.data.Story
import com.app.storyapp.nonui.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail: LiveData<Story> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStories()

                // Log untuk debugging
                Log.d("StoryViewModel", "Stories response: ${response.listStory?.size}")

                _stories.value = response.listStory ?: emptyList()
                _error.value = null
            } catch (e: Exception) {
                // Log error detail
                Log.e("StoryViewModel", "Error fetching stories", e)
                _error.value = when (e) {
                    is retrofit2.HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        "HTTP Error: ${e.code()} - $errorBody"
                    }
                    else -> e.message ?: "Terjadi kesalahan"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getStoryDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStoryDetail(id)
                _storyDetail.value = response.story!!
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }
}