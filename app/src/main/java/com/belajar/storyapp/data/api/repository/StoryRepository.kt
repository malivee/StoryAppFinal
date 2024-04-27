package com.belajar.storyapp.data.api.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.data.api.response.LoginResponse
import com.belajar.storyapp.data.api.response.RegisterResponse
import com.belajar.storyapp.data.api.response.UploadResponse
import com.belajar.storyapp.data.api.retrofit.ApiConfig
import com.belajar.storyapp.data.api.retrofit.ApiService
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.AuthPreference
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val authPreference: AuthPreference
) {

    fun postRegister(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.register(name, email, password)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
//                emit(Result.Failure(client.message.toString()))
                Log.e("PostRegister", "${client.message}")
            }
        } catch (e: Exception) {
            emit(Result.Failure(e.message.toString()))
            Log.e("PostRegisterHttp", e.message.toString())
        }
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.login(email, password)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                Log.e("PostLogin", "${client.message}")
                emit(Result.Failure(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostLoginHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        }
    }

    fun getStories(): LiveData<Result<AllStoryResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.getStories()
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                Log.e("GetStories", "${client.message}")
                emit(Result.Failure(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostLoginHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        }
    }

    fun postStory(multipartBody: MultipartBody.Part, description: RequestBody): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)


        try {
            val client = apiService.postStory(multipartBody, description)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                emit(Result.Failure(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostLoginHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        }
    }

    fun postStoryGuest(multipartBody: MultipartBody.Part, description: RequestBody): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.postStoryGuest(multipartBody, description)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                emit(Result.Failure(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostLoginHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        }
    }

    suspend fun saveData(dataModel: DataModel) {
        authPreference.saveData(dataModel)
    }

    fun getData(): Flow<DataModel> {
        return authPreference.getData()
    }

    suspend fun logout() {
        authPreference.logout()
    }

    suspend fun getToken() {

    }

    companion object {
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, authPreference: AuthPreference): StoryRepository =
            instance?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPreference)
            }.also { instance = it }
    }
}