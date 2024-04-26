package com.belajar.storyapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.RegisterResponse
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.launch

class RegisterViewModel(private val storyRepository: StoryRepository): ViewModel() {
    private lateinit var _isLoading: MutableLiveData<Boolean>
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun postRegister(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = storyRepository.postRegister(name, email, password)
//        _isLoading.value = false
//        viewModelScope.launch {
//            storyRepository.postRegister(name, email, password)
//            _isLoading.value = true
//        }
//    }

}