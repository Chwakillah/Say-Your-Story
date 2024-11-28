package com.app.storyapp.ui

import android.os.Bundle
import android.transition.Slide
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.storyapp.databinding.ActivityStoryDetailBinding
import com.app.storyapp.nonui.data.ListStoryItem
import com.app.storyapp.nonui.data.Story
import com.app.storyapp.nonui.di.Injection
import com.app.storyapp.nonui.viewmodel.StoryViewModel
import com.app.storyapp.nonui.viewmodel.ViewModelFactory
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    private val viewModel: StoryViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.allowEnterTransitionOverlap = true
        window.allowReturnTransitionOverlap = true

        val slideTransition = Slide()
        slideTransition.duration = 300
        window.enterTransition = slideTransition
        window.returnTransition = slideTransition

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get story from intent
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)?.let {
            Story(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                lon = it.lon,
                lat = it.lat
            )
        }

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)

        if (story != null) {
            // If story object is passed, show it directly
            showStoryDetail(story)
        } else if (storyId != null) {
            // If only ID is passed, fetch from API
            viewModel.getStoryDetail(storyId)
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.storyDetail.observe(this) { story ->
            story?.let { showStoryDetail(it) }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showStoryDetail(story: Story) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description

            // Format date
            story.createdAt?.let { createdAt ->
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
                try {
                    val date = inputFormat.parse(createdAt)
                    date?.let {
                        tvDetailDate.text = outputFormat.format(it)
                    }
                } catch (e: Exception) {
                    tvDetailDate.text = createdAt
                }
            }

            // Load image
            Glide.with(this@StoryDetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}