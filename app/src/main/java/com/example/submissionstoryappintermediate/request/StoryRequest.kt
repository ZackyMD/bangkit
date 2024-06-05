package com.example.submissionstoryappintermediate.request

data class StoryRequest(
    val id: String?,
    val name: String,
    val description: String,
    val photoUrl: String?,
    val createdAt: String?,
    val lat: Double?,
    val lon: Double?
)