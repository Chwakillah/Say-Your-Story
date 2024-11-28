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
        // More comprehensive token retrieval with detailed logging
        val token = try {
            val retrievedToken = userPreferences.getToken().first()
            Log.d("StoryRepository", "Token retrieval attempt: '$retrievedToken'")

            if (retrievedToken.isBlank()) {
                Log.e("StoryRepository", "Token is blank")
                throw IllegalStateException("Authentication token is empty")
            }

            retrievedToken
        } catch (e: Exception) {
            Log.e("StoryRepository", "Error retrieving token", e)
            throw IllegalStateException("Failed to retrieve authentication token: ${e.message}")
        }

        // Log the token being used
        Log.d("StoryRepository", "Using token for API call: '$token'")

        val authenticatedApiService = ApiConfig.getApiService(token)
        return authenticatedApiService.getStories()
    }

    suspend fun getStoryDetail(id: String): StoryDetailResponse {
        return apiService.getStoryDetail(id)
    }

    suspend fun uploadStory(description: RequestBody, photo: MultipartBody.Part): StoryResponse {
        // Retrieve and use the authenticated token, similar to getStories()
        val token = try {
            val retrievedToken = userPreferences.getToken().first()
            Log.d("StoryRepository", "Upload Token retrieval attempt: '$retrievedToken'")

            if (retrievedToken.isBlank()) {
                Log.e("StoryRepository", "Token is blank for upload")
                throw IllegalStateException("Authentication token is empty")
            }

            retrievedToken
        } catch (e: Exception) {
            Log.e("StoryRepository", "Error retrieving token for upload", e)
            throw IllegalStateException("Failed to retrieve authentication token: ${e.message}")
        }

        // Create authenticated API service for upload
        val authenticatedApiService = ApiConfig.getApiService(token)
        Log.d("StoryRepository", "Using token for upload: '$token'")

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