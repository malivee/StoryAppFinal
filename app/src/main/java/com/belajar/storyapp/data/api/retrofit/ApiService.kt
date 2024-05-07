package com.belajar.storyapp.data.api.retrofit

import com.belajar.storyapp.data.api.response.AllStoryResponse
import com.belajar.storyapp.data.api.response.DetailResponse
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.data.api.response.LoginResponse
import com.belajar.storyapp.data.api.response.RegisterResponse
import com.belajar.storyapp.data.api.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse


    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): AllStoryResponse

    @GET("stories/{id}")
    suspend fun getDetailedStory(
        @Path("id") id: String
    ): DetailResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadResponse

    @Multipart
    @POST("stories/guest")
    suspend fun postStoryGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadResponse
}