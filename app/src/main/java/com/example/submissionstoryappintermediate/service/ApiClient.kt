package com.example.submissionstoryappintermediate.service

import com.example.submissionstoryappintermediate.response.LoginResponse
import com.example.submissionstoryappintermediate.response.LoginResult
import com.example.submissionstoryappintermediate.response.StoryDetailResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient(private val token: String? = null) {
    private val BASE_URL = "https://story-api.dicoding.dev/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val req = chain.request()
        val requestBuilder = req.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .apply {
            if (!token.isNullOrEmpty()) {
                addInterceptor(authInterceptor)
            }
        }
        .build()

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    // Function to register user
    suspend fun register(name: String, email: String, password: String) {
        apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStoryDetail(storyId: String): StoryDetailResponse {
        return apiService.getStoryDetail(storyId)
    }

}
