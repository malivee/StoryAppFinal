package com.belajar.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.helper.Result

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getMapStories(): LiveData<Result<AllStoryResponse>> = storyRepository.getMapStories()
}