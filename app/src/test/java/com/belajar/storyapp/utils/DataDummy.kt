package com.belajar.storyapp.utils

import com.belajar.storyapp.data.api.response.ListStoryItem

object DataDummy {
    fun generateDummyResponse(): List<ListStoryItem> {
        val storyList: MutableList<ListStoryItem> = arrayListOf()
        for (i in 1..100) {
            val story =
                ListStoryItem(
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/Wikipedia-logo-v2-en.svg/892px-Wikipedia-logo-v2-en.svg.png",
                    "2022-02-22T22:22:22Z",
                    "Name $i",
                    "Description $i",
                    -5.132193,
                    "id $i",
                    119.488449,
                    )
            storyList.add(story)
        }
        return storyList
    }
}