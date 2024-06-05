package com.example.submissionstoryappintermediate.response

import com.example.submissionstoryappintermediate.request.StoryDetailRequest

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: StoryDetailRequest?
)