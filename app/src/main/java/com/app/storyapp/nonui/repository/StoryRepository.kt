package com.app.storyapp.nonui.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.app.storyapp.nonui.data.ListStoryItem
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
    suspend fun getStoriesWithLocation(): StoryResponse {
        val token = userPreferences.getToken().first()
        val authenticatedApiService = ApiConfig.getApiService(token)
        return authenticatedApiService.getStoriesWithLocation()
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        val tokenFlow = userPreferences.getToken()
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                val token = runBlocking {
                    tokenFlow.first()
                }
                val authenticatedApiService = ApiConfig.getApiService(token)
                StoryPagingSource(authenticatedApiService, token)
            }
        ).liveData
    }


    suspend fun getStoryDetail(id: String): StoryDetailResponse {
        return apiService.getStoryDetail(id)
    }

    suspend fun uploadStory(description: RequestBody, photo: MultipartBody.Part): StoryResponse {
        val token = userPreferences.getToken().first()
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