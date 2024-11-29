package com.app.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.storyapp.databinding.ActivityLoginBinding
import com.app.storyapp.nonui.repository.AuthRepository
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.utils.UserPreferences
import com.app.storyapp.nonui.utils.dataStore
import com.app.storyapp.nonui.viewmodel.LoginViewModel
import com.app.storyapp.nonui.viewmodel.RegisterViewModel
import com.app.storyapp.ui.HomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreferences

    private val loginViewModel: LoginViewModel by lazy {
        LoginViewModel(
            AuthRepository(ApiConfig.api),
            UserPreferences.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences.getInstance(dataStore)

        loginViewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        val passwordEditText = binding.edLoginPassword
        val emailEditText = binding.edLoginEmail

        emailEditText.setInputLayout(binding.edLoginEmailLayout)
        passwordEditText.setInputLayout(binding.edLoginPasswordLayout)

        lifecycleScope.launch {
            userPreferences.getName().collect { name ->
                Log.d("LoginActivity", "Current saved name: $name")
            }
        }

        loginViewModel.loginResponse.observe(this) { response ->
            if (response.error == false) {
                response.loginResult?.let { loginResult ->
                    lifecycleScope.launch {
                        val token = loginResult.token ?: ""
                        if (token.isNotEmpty()) {
                            userPreferences.saveLoginSession(token)
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed: Empty token", Toast.LENGTH_SHORT).show()
                        }
                        loginResult.name?.let {
                            userPreferences.saveName(it)
                        }

                        Toast.makeText(this@LoginActivity, "Halo ${loginResult.name}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        binding.btnMasuk.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
