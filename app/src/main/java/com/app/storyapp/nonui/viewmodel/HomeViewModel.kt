package com.app.storyapp.nonui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.app.storyapp.nonui.data.ListStoryItem
import com.app.storyapp.nonui.repository.StoryRepository

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val stories: LiveData<PagingData<ListStoryItem>> = storyRepository.getStories()
}