package com.app.storyapp.nonui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.storyapp.nonui.data.LoginResponse
import com.app.storyapp.nonui.data.RegisterResponse
import com.app.storyapp.nonui.repository.AuthRepository
import com.app.storyapp.nonui.utils.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences // Tambahkan parameter ini
) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = authRepository.login(email, password)
                _loginResponse.value = response

                // Pastikan menyimpan nama pengguna saat login berhasil
                if (response.error == false) {
                    response.loginResult?.name?.let { name ->
                        userPreferences.saveName(name)
                        Log.d("LoginViewModel", "Saved name: $name")
                    }
                }
            } catch (e: Exception) {
                _loginResponse.value = LoginResponse(error = true, message = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

