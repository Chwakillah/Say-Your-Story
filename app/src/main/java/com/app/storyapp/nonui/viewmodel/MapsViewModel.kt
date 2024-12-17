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
                val response = repository.getStoriesWithLocation()
                val filteredStories = response.listStory.filter {
                    it.lat != null && it.lon != null
                }
                _storiesWithLocation.postValue(filteredStories)
            } catch (e: Exception) {
                _storiesWithLocation.postValue(emptyList())
            }
        }
    }
}