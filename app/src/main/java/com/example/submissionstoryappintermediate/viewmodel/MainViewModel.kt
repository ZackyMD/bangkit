package com.example.submissionstoryappintermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionstoryappintermediate.repository.StoryRepository
import com.example.submissionstoryappintermediate.request.StoryRequest
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(): LiveData<PagingData<StoryRequest>> {
        return storyRepository.getStories().cachedIn(viewModelScope)
    }

    fun getStoriesFlow(): Flow<PagingData<StoryRequest>> {
        return storyRepository.getStoriesFlow().cachedIn(viewModelScope)
    }
}

