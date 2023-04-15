package com.jaylangkung.korem.dataClass

data class GiatResponse(
    val data: ArrayList<GiatData>,
    val status: String,
    val message: String
)