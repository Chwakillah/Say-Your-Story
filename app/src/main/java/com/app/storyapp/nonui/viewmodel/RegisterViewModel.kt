package com.app.storyapp.nonui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.RegisterResponse
import com.app.storyapp.nonui.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> get() = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            try {
                val response = repository.register(name, email, password)
                _registerResponse.postValue(response)
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error: ${e.message}")
                _registerResponse.postValue(RegisterResponse(error = true))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}



