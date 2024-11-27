package com.app.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.storyapp.databinding.ItemStoryBinding
import com.app.storyapp.nonui.data.ListStoryItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter(private val onItemClick: (ListStoryItem, View) -> Unit) :
    ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StoryViewHolder(
        private val binding: ItemStoryBinding,
        private val onItemClick: (ListStoryItem, View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                tvName.text = story.name
                tvDescription.text = story.description

                // Format date
                story.createdAt?.let { createdAt ->
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                    try {
                        val date = inputFormat.parse(createdAt)
                        date?.let { tvDate.text = outputFormat.format(it) }
                    } catch (e: Exception) {
                        tvDate.text = createdAt
                    }
                }

                // Load image using Glide
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivStory)

                // Handle click
                itemView.setOnClickListener { onItemClick(story, itemView) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}