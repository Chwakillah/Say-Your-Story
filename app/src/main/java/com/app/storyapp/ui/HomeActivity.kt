package com.app.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.app.storyapp.nonui.viewmodel.HomeViewModel
import com.app.storyapp.nonui.viewmodel.ViewModelFactory
import com.app.storyapp.ui.adapter.LoadingStateAdapter
import com.app.storyapp.ui.adapter.StoryAdapter
import com.app.storyapp.ui.auth.LoginActivity
import com.app.storyapp.ui.components.MapsActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var storyAdapter: StoryAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { story, itemView ->
            val intent = Intent(this, StoryDetailActivity::class.java).apply {
                putExtra(StoryDetailActivity.EXTRA_STORY, story)
            }

            val ivStory = itemView.findViewById<View?>(R.id.iv_story_photo)
            val tvName = itemView.findViewById<View?>(R.id.tv_item_name)
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

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            storyAdapter.refresh()
        }
    }

    private fun observeViewModel() {
        homeViewModel.stories.observe(this) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }

        lifecycleScope.launch {
            storyAdapter.loadStateFlow.collect { loadStates ->
                val isLoading = loadStates.refresh is androidx.paging.LoadState.Loading
                showLoading(isLoading)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            val userPreferences = UserPreferences.getInstance(dataStore)
            userPreferences.clearLoginSession()
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