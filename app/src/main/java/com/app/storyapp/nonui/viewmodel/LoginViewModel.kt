package com.app.storyapp.nonui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.LoginResponse
import com.app.storyapp.nonui.data.RegisterResponse
import com.app.storyapp.nonui.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            try {
                val response = repository.login(email, password)
                _loginResponse.postValue(response)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during login: ${e.message}")
                _loginResponse.postValue(LoginResponse(error = true, message = "Login failed"))
            }
        }
    }
}

