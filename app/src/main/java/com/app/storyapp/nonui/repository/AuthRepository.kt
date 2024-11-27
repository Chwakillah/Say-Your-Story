package com.app.storyapp.nonui.repository

import com.app.storyapp.nonui.data.LoginResponse
import com.app.storyapp.nonui.data.RegisterResponse
import com.app.storyapp.nonui.retrofit.ApiService

class AuthRepository(private val apiService: ApiService) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }
}
