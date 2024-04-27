package com.belajar.storyapp.di

import android.content.Context
import android.content.Intent
import android.util.Log
import com.belajar.storyapp.LoginActivity
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.retrofit.ApiConfig
import com.belajar.storyapp.helper.AuthPreference
import com.belajar.storyapp.helper.dataStore

object Injection {

    fun provideRepository(context: Context): StoryRepository {
//        val intent = Intent()
//        val token = intent.getStringExtra(LoginActivity.EXTRA_RESULT)
//        Log.i("TOKEN", "token: $token")
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(authPreference)
        return StoryRepository.getInstance(apiService, authPreference)
    }
}