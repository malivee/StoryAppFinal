package com.belajar.storyapp.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.launch

class HomepageViewModel(private val storyRepository: StoryRepository) : ViewModel() {
//    fun getStories(): LiveData<Result<AllStoryResponse>> = storyRepository.getStories()
    val stories: LiveData<PagingData<ListStoryItem>> = storyRepository.getStories().cachedIn(viewModelScope)

    fun getLoginData(): LiveData<DataModel> {
        return storyRepository.getData().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }

}