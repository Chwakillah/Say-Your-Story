package com.app.storyapp.nonui.repository

import android.util.Log
import com.app.storyapp.nonui.data.StoryDetailResponse
import com.app.storyapp.nonui.data.StoryResponse
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.retrofit.ApiService
import com.app.storyapp.nonui.utils.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun getStories(): StoryResponse {
        val token = try {
            val retrievedToken = userPreferences.getToken().first()

            if (retrievedToken.isBlank()) {
                throw IllegalStateException("Authentication token is empty")
            }

            retrievedToken
        } catch (e: Exception) {
            throw IllegalStateException("Failed to retrieve authentication token: ${e.message}")
        }


        val authenticatedApiService = ApiConfig.getApiService(token)
        return authenticatedApiService.getStories()
    }

    suspend fun getStoryDetail(id: String): StoryDetailResponse {
        return apiService.getStoryDetail(id)
    }

    suspend fun uploadStory(description: RequestBody, photo: MultipartBody.Part): StoryResponse {
        val token = try {
            val retrievedToken = userPreferences.getToken().first()

            if (retrievedToken.isBlank()) {
                throw IllegalStateException("Authentication token is empty")
            }

            retrievedToken
        } catch (e: Exception) {
            throw IllegalStateException("Failed to retrieve authentication token: ${e.message}")
        }

        val authenticatedApiService = ApiConfig.getApiService(token)

        return authenticatedApiService.addStory(description, photo)
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