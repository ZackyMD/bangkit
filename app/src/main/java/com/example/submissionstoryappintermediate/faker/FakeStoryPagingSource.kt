package com.example.submissionstoryappintermediate.faker

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissionstoryappintermediate.request.StoryRequest

class FakeStoryPagingSource : PagingSource<Int, StoryRequest>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryRequest> {
        return LoadResult.Page(
            data = listOf(
                StoryRequest(id = "1", name = "Story 1", description = "Description 1", createdAt = "3 Juni 2024", photoUrl = "url1", lat = 0.0, lon = 0.5),
                StoryRequest(id = "2", name = "Story 2", description = "Description 2", createdAt = "3 Juni 2024", photoUrl = "url2", lat = 0.0, lon = 0.2)
            ),
            prevKey = null,
            nextKey = null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, StoryRequest>): Int? {
        return state.anchorPosition
    }
}
