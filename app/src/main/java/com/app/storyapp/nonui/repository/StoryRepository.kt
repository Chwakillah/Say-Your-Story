package com.app.storyapp.nonui.repository

import com.app.storyapp.nonui.data.StoryResponse
import com.app.storyapp.nonui.retrofit.ApiService
import com.app.storyapp.nonui.utils.UserPreferences
import kotlinx.coroutines.flow.first

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences)
            }.also { instance = it }
    }
}
