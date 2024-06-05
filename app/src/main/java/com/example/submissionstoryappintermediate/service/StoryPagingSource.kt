package com.example.submissionstoryappintermediate.service

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissionstoryappintermediate.request.StoryRequest

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, StoryRequest>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryRequest> {
        return try {
            val position = params.key ?: 1
            val response = apiService.getStories(position, params.loadSize)
            LoadResult.Page(
                data = response.listStory,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryRequest>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
