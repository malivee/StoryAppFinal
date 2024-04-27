package com.belajar.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.data.api.response.DetailResponse
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.helper.Result

class DetailViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getDetailStory(id: String): LiveData<Result<DetailResponse>> = storyRepository.getDetailStory(id)
}