package com.example.submissionstoryappintermediate.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)