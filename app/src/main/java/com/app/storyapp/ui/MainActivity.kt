package com.app.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.storyapp.ui.auth.LoginActivity
import com.app.storyapp.ui.HomeActivity
import com.app.storyapp.nonui.utils.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        // Tambahkan log untuk memeriksa status login
        val isLoggedIn = sessionManager.isLoggedIn()
        val token = sessionManager.getToken()

        Log.d("MainActivity", "isLoggedIn: $isLoggedIn")
        Log.d("MainActivity", "Token: $token")

        if (isLoggedIn) {
            // Jika sudah login, langsung ke HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // Jika belum login, arahkan ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Tutup MainActivity
    }
}

