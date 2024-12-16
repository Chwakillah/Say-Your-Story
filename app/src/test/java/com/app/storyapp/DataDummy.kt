package com.app.storyapp

import com.app.storyapp.nonui.data.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                photoUrl = "url_foto_$i",
                createdAt = "16/12/2024",
                name = "name $i",
                description = "tes",
                lon = 0.034,
                lat = 0.02,
                id = i.toString()
            )
            items.add(quote)
        }
        return items
    }
}