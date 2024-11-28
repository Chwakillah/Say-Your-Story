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
        val passwordLayout = binding.edLoginPasswordLayout
        val emailEditText = binding.edLoginEmail
        val emailLayout = binding.edLoginEmailLayout

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length < 8) {
                    passwordLayout.error = "Password tidak boleh kurang dari 8 karakter"
                } else {
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailLayout.error = "Email tidak valid"
                } else {
                    emailLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

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
                        Log.d("LoginActivity", "Received token: '$token'")

                        if (token.isNotEmpty()) {
                            userPreferences.saveLoginSession(token)
                            Log.d("LoginActivity", "Token saved successfully")
                        } else {
                            Log.e("LoginActivity", "Received empty token")
                            Toast.makeText(this@LoginActivity, "Login failed: Empty token", Toast.LENGTH_SHORT).show()
                        }

                        // Simpan nama
                        loginResult.name?.let {
                            userPreferences.saveName(it)
                            Log.d("LoginActivity", "Name saved: $it")
                        }

                        Toast.makeText(this@LoginActivity, "Halo ${loginResult.name}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }


        // Menangani klik tombol login
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
        Log.d("RegisterActivity", "isLoading: $isLoading")
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
