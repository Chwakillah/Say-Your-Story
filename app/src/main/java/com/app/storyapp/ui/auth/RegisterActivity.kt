package com.app.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.storyapp.databinding.ActivityRegisterBinding
import com.app.storyapp.nonui.repository.AuthRepository
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.utils.UserPreferences
import com.app.storyapp.nonui.utils.dataStore
import com.app.storyapp.nonui.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userPreferences: UserPreferences

    private val registerViewModel: RegisterViewModel by lazy {
        RegisterViewModel(AuthRepository(ApiConfig.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences.getInstance(dataStore)

        val emailEditText = binding.edRegisterEmail
        val passwordEditText = binding.edRegisterPassword

        emailEditText.setInputLayout(binding.edRegisterEmailLayout)
        passwordEditText.setInputLayout(binding.edRegisterPasswordLayout)

        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        registerViewModel.registerResponse.observe(this) { response ->
            if (response.error == true) {
                Toast.makeText(
                    this,
                    response.message ?: "Terjadi kesalahan saat mendaftar;-;",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch {
                    userPreferences.saveName(binding.edRegisterNama.text.toString())
                }

                Toast.makeText(
                    this,
                    "Registrasi berhasil! Silakan login",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnDaftar.setOnClickListener {
            val name = binding.edRegisterNama.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edRegisterNamaLayout.error = "Nama tidak boleh kosong"
                }
                email.isEmpty() -> {
                    binding.edRegisterEmailLayout.error = "Email tidak boleh kosong"
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.edRegisterEmailLayout.error = "Email tidak valid"
                }
                password.isEmpty() -> {
                    binding.edRegisterPasswordLayout.error = "Password tidak boleh kosong"
                }
                password.length < 8 -> {
                    binding.edRegisterPasswordLayout.error = "Password minimal 8 karakter"
                }
                else -> {
                    registerViewModel.register(name, email, password)
                }
            }
        }

        binding.btnMasuk.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}