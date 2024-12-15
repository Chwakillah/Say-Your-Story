package com.app.storyapp.nonui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.ListStoryItem
import com.app.storyapp.nonui.repository.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> get() = _storiesWithLocation

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val response = repository.getStories()
                val filteredStories = response.listStory.filter {
                    it.lat != null && it.lon != null // Filter stories with valid coordinates
                }
                _storiesWithLocation.postValue(filteredStories)
            } catch (e: Exception) {
                // Handle error (log or show a message to user)
                _storiesWithLocation.postValue(emptyList())
            }
        }
    }
}
