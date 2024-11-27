package com.app.storyapp.nonui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.storyapp.nonui.repository.StoryRepository

class ViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}