package com.belajar.storyapp.data.api.retrofit

import android.util.Log
import com.belajar.storyapp.BuildConfig
import com.belajar.storyapp.data.model.DataModel
import com.belajar.storyapp.helper.AuthPreference
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    fun getApiService(authPreference: AuthPreference): ApiService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        val authInterceptor = Interceptor {
            val request = it.request()
            val token = runBlocking {
                val tokenValue = authPreference.getData().firstOrNull()?.token ?: ""
                Log.i("TOKENINAPI", tokenValue)
                tokenValue
            }
            val header = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            it.proceed(header)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}