package com.belajar.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.RegisterResponse
import com.belajar.storyapp.helper.Result

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun postRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = storyRepository.postRegister(name, email, password)
}