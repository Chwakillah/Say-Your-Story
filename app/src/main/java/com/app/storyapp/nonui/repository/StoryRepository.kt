package com.app.storyapp.nonui.repository

import com.app.storyapp.nonui.data.StoryDetailResponse
import com.app.storyapp.nonui.data.StoryResponse
import com.app.storyapp.nonui.retrofit.ApiService
import com.app.storyapp.nonui.utils.UserPreferences

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    suspend fun getStoryDetail(id: String): StoryDetailResponse {
        return apiService.getStoryDetail(id)
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