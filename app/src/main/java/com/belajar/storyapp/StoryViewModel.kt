package com.belajar.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.UploadResponse
import com.belajar.storyapp.helper.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun postStory(
        token: String,
        multipartBody: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<UploadResponse>> =
        storyRepository.postStory(token, multipartBody, description)
}