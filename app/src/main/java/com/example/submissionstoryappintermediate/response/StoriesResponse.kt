package com.example.submissionstoryappintermediate.response

import com.example.submissionstoryappintermediate.request.StoryRequest

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryRequest>
)