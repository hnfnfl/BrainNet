package com.jaylangkung.korem.dataClass

data class UserResponse(
    val data: List<UserData>,
    val message: String,
    val status: String,
    val tokenAuth: String
)