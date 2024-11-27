package com.app.storyapp.nonui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.ListStoryItem
import com.app.storyapp.nonui.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStories()
                _stories.value = response.listStory
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }
}