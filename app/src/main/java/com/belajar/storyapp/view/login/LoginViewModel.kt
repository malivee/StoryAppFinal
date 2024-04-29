package com.belajar.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.response.LoginResponse
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> =
        storyRepository.postLogin(email, password)

    fun saveData(dataModel: DataModel) {
        viewModelScope.launch {
            storyRepository.saveData(dataModel)
        }
    }
}