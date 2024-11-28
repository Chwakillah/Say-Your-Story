package com.app.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.storyapp.R
import com.app.storyapp.databinding.ActivityHomeBinding
import com.app.storyapp.nonui.di.Injection
import com.app.storyapp.nonui.utils.UserPreferences
import com.app.storyapp.nonui.utils.dataStore
import com.app.storyapp.nonui.viewmodel.StoryViewModel
import com.app.storyapp.nonui.viewmodel.ViewModelFactory
import com.app.storyapp.ui.adapter.StoryAdapter
import com.app.storyapp.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val storyAdapter = StoryAdapter { story, itemView ->
        val intent = Intent(this, StoryDetailActivity::class.java).apply {
            putExtra(StoryDetailActivity.EXTRA_STORY, story)
        }

        // Periksa apakah elemen-elemen yang dibutuhkan ada
        val ivStory = itemView.findViewById<View?>(R.id.ivStory)
        val tvName = itemView.findViewById<View?>(R.id.tvName)
        val tvDescription = itemView.findViewById<View?>(R.id.tvDescription)

        val options = if (ivStory != null && tvName != null && tvDescription != null) {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                androidx.core.util.Pair(ivStory, "story_image"),
                androidx.core.util.Pair(tvName, "story_name"),
                androidx.core.util.Pair(tvDescription, "story_description")
            )
        } else {
            ActivityOptionsCompat.makeBasic()
        }

        startActivity(intent, options.toBundle())
    }

    private val viewModel: StoryViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        viewModel.getStories()
        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getStories()
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { stories ->
            storyAdapter.submitList(stories)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            val userPreferences = UserPreferences.getInstance(dataStore)
            userPreferences.clearLoginSession() // Ini akan menghapus token dan nama

            Toast.makeText(this@HomeActivity, "Logout berhasil", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = isLoading
    }
}