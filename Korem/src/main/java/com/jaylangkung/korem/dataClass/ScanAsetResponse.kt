package com.jaylangkung.korem.dataClass

data class ScanAsetResponse(
    val data: ArrayList<ScanAsetData>,
    val status: String,
    val message: String,
)