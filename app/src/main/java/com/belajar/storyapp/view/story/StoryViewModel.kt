package com.belajar.storyapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.UploadResponse
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun postStory(
        multipartBody: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<UploadResponse>> =
        storyRepository.postStory(multipartBody, description)

    fun getLoginData(): LiveData<DataModel> {
        return storyRepository.getData().asLiveData()
    }

    fun postStoryGuest(
        multipartBody: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<UploadResponse>> =
        storyRepository.postStoryGuest(multipartBody, description)

}