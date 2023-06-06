package com.jaylangkung.korem.dataClass

data class PostResponse(
    val data: ArrayList<PostData>,
    val status: String
)

data class PostData(
    val judul: String,
    val img: String,
    val url: String,
)