package com.belajar.storyapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.UploadResponse
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<Result<UploadResponse>>()
    val stories: LiveData<Result<UploadResponse>> get() = _stories

    private val _storiesGuest = MutableLiveData<Result<UploadResponse>>()
    val storiesGuest: LiveData<Result<UploadResponse>> get() = _storiesGuest
    fun postStory(
        multipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        viewModelScope.launch {
            storyRepository.postStory(multipartBody, description, lat, lon).observeForever {
                _stories.value = it
            }

        }
    }

    fun getLoginData(): LiveData<DataModel> {
        return storyRepository.getData().asLiveData()
    }

    fun postStoryGuest(
        multipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        viewModelScope.launch {
            storyRepository.postStoryGuest(multipartBody, description, lat, lon).observeForever {
                _storiesGuest.value = it
            }
        }

    }
}