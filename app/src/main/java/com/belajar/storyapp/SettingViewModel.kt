package com.belajar.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

class SettingViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }

    fun getData(): LiveData<DataModel> {
        return storyRepository.getData().asLiveData()
    }
}