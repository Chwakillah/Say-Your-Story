package com.app.storyapp.nonui.di

import android.content.Context
import com.app.storyapp.nonui.repository.StoryRepository
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.utils.UserPreferences
import com.app.storyapp.nonui.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService, pref)
    }
}