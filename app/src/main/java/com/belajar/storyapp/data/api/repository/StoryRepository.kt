package com.belajar.storyapp.data.api.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.data.api.response.DetailResponse
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.data.api.response.LoginResponse
import com.belajar.storyapp.data.api.response.RegisterResponse
import com.belajar.storyapp.data.api.response.UploadResponse
import com.belajar.storyapp.data.api.retrofit.ApiService
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.data.paging.StoryRemoteMediator
import com.belajar.storyapp.data.room.StoryDatabase
import com.belajar.storyapp.helper.AuthPreference
import com.belajar.storyapp.helper.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val authPreference: AuthPreference,
    private val database: StoryDatabase
) {

    fun postRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.register(name, email, password)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun getStories(): LiveData<Result<AllStoryResponse>> = liveData {
////        @OptIn(ExperimentalPagingApi::class)
////        Pager(
////            PagingConfig(
////                pageSize = 5
////            ),
////            remoteMediator = StoryRemoteMediator(apiService)
////        )
//        emit(Result.Loading)
//
//        try {
//            val client = apiService.getStories(5, 10)
//            if (client.error == false) {
//                emit(Result.Success(client))
//            } else {
//                Log.e("GetStories", "${client.message}")
//                emit(Result.Failure(client.message.toString()))
//            }
//        } catch (e: HttpException) {
//            Log.e("GetStoriesHTTP", "${e.message}")
//            emit(Result.Failure(e.message.toString()))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
fun getStories(): LiveData<PagingData<ListStoryItem>> {
    @OptIn(ExperimentalPagingApi::class)
    return Pager(
            PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(apiService = apiService, storyDatabase = database),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getDetailStory(id: String): LiveData<Result<DetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.getDetailedStory(id)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                emit(Result.Failure(client.message.toString()))
            }

        } catch (e: HttpException) {
            Log.e("DetailStoriesHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun postStory(
        multipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)


        try {
            val client = apiService.postStory(multipartBody, description, lat, lon)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                emit(Result.Failure(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostStoryHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun postStoryGuest(
        multipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)

        try {
            val client = apiService.postStoryGuest(multipartBody, description, lat, lon)
            if (client.error == false) {
                emit(Result.Success(client))
            } else {
                emit(Result.Failure(client.message.toString()))
            }
        } catch (e: HttpException) {
            Log.e("PostStoryGuestHTTP", "${e.message}")
            emit(Result.Failure(e.message.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
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


    companion object {
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, authPreference: AuthPreference, database: StoryDatabase): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPreference, database)
            }.also { instance = it }
    }
}