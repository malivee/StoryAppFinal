package com.belajar.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.DetailResponse
import com.belajar.storyapp.helper.Result

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getDetailStory(id: String): LiveData<Result<DetailResponse>> =
        storyRepository.getDetailStory(id)
}