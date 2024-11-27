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
import com.app.storyapp.databinding.ActivityLoginBinding
import com.app.storyapp.nonui.repository.AuthRepository
import com.app.storyapp.nonui.retrofit.ApiConfig
import com.app.storyapp.nonui.utils.SessionManager
import com.app.storyapp.nonui.viewmodel.LoginViewModel
import com.app.storyapp.nonui.viewmodel.RegisterViewModel
import com.app.storyapp.ui.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Membuat instance LoginViewModel menggunakan ViewModel
    private val loginViewModel: LoginViewModel by lazy {
        LoginViewModel(AuthRepository(ApiConfig.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        val passwordEditText = binding.edLoginPassword
        val passwordLayout = binding.edLoginPasswordLayout
        val emailEditText = binding.edLoginEmail
        val emailLayout = binding.edLoginEmailLayout

        // Validasi input email dan password
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

        // Observasi hasil login dari ViewModel
        // Tambahkan ini di bagian login sukses di LoginActivity
        loginViewModel.loginResponse.observe(this, Observer { response ->
            if (response.error == true) {
                Toast.makeText(this, response.message ?: "Ada yang salah. Coba lagi", Toast.LENGTH_SHORT).show()
            } else {
                // Simpan token dan status login
                val sessionManager = SessionManager(this)
                response.loginResult?.token?.let { token ->
                    sessionManager.saveSession(token)
                    Log.d("LoginActivity", "Token saved: $token")
                }

                Toast.makeText(this, "Halo lagi!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })




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

        // Menangani klik untuk pindah ke RegisterActivity
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
