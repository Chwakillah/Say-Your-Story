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

        setupValidation()
        setupObservers()
        setupButtons()
    }

    private fun setupValidation() {
        // Name validation
        binding.edRegisterNama.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.edRegisterNamaLayout.error = if (s.isNullOrEmpty()) {
                    "Nama tidak boleh kosong"
                } else null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Email validation
        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.edRegisterEmailLayout.error = when {
                    s.isNullOrEmpty() -> "Email tidak boleh kosong"
                    !Patterns.EMAIL_ADDRESS.matcher(s).matches() -> "Email tidak valid"
                    else -> null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Password validation
        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.edRegisterPasswordLayout.error = when {
                    s.isNullOrEmpty() -> "Password tidak boleh kosong"
                    s.length < 8 -> "Password minimal 8 karakter"
                    else -> null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        registerViewModel.registerResponse.observe(this) { response ->
            if (response.error == true) {
                Toast.makeText(
                    this,
                    response.message ?: "Terjadi kesalahan saat mendaftar",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Registrasi berhasil! Silakan login",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to Login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupButtons() {
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