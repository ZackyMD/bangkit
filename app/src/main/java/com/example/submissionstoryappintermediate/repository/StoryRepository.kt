package com.example.submissionstoryappintermediate.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionstoryappintermediate.request.StoryRequest
import com.example.submissionstoryappintermediate.service.ApiService
import com.example.submissionstoryappintermediate.service.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val apiService: ApiService, private val token: String) {
    private val savedStories = mutableListOf<StoryRequest>()

    fun getStories(): LiveData<PagingData<StoryRequest>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData
    }

    fun getStoriesFlow(): Flow<PagingData<StoryRequest>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).flow
    }

    fun saveData(data: List<StoryRequest>) {
        savedStories.clear()
        savedStories.addAll(data)
    }

    fun getSavedStories(): List<StoryRequest> {
        return savedStories.toList()
    }
}
