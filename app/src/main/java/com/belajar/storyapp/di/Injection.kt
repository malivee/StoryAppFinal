package com.belajar.storyapp.di

import android.content.Context
import com.belajar.storyapp.data.api.repository.StoryRepository
import com.belajar.storyapp.data.api.retrofit.ApiConfig
import com.belajar.storyapp.data.room.StoryDatabase
import com.belajar.storyapp.helper.AuthPreference
import com.belajar.storyapp.helper.dataStore

object Injection {

    fun provideRepository(context: Context): StoryRepository {
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(authPreference)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, authPreference, database)
    }
}