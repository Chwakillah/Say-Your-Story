package com.app.storyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.storyapp.nonui.di.Injection
import com.app.storyapp.nonui.utils.UserPreferences
import com.app.storyapp.nonui.utils.dataStore
import com.app.storyapp.nonui.viewmodel.StoryViewModel
import com.app.storyapp.nonui.viewmodel.ViewModelFactory
import com.app.storyapp.ui.auth.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences
    private val viewModel: StoryViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences.getInstance(dataStore)

        lifecycleScope.launch {
            checkLoginState()
        }
    }

    private suspend fun checkLoginState() {
        val isLoggedIn = userPreferences.getLoginState().first()
        val intent = if (isLoggedIn) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}


