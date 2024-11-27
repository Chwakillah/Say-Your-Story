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
import com.app.storyapp.R
import com.app.storyapp.databinding.ActivityRegisterBinding
import com.app.storyapp.nonui.repository.AuthRepository
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by lazy {
        RegisterViewModel(AuthRepository(ApiConfig.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })


        val passwordEditText = binding.edRegisterPassword
        val passwordLayout = binding.edRegisterPasswordLayout
        val emailEditText = binding.edRegisterEmail
        val emailLayout = binding.edRegisterEmailLayout

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

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
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailLayout.error = "Email tidak valid"
                } else {
                    emailLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        registerViewModel.registerResponse.observe(this, Observer { response ->
            if (response.error == true) {

                Toast.makeText(this, response.message ?: "Email udah terdaftar, nih! Coba yang lain, dong:<", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Selamat Bergabung!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        binding.btnDaftar.setOnClickListener {
            val name = binding.edRegisterNama.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(this, "Lengkapi dulu semua datanya, ya!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMasuk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        Log.d("RegisterActivity", "isLoading: $isLoading")
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
