package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.data.api.ListStoryItem

object DataDummy {
    fun generateDummyStoryEntity() : List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photo + $i",
                "created + $i",
                "name + $i",
                "desc + $i",
                i.toDouble(),
                i.toString(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}