package com.jaylangkung.korem.dataClass

data class UserResponse(
    val data: ArrayList<UserData>,
    val message: String,
    val status: String,
    val tokenAuth: String
)