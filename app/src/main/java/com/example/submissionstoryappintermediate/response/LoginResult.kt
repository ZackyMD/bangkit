package com.example.submissionstoryappintermediate.response

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)