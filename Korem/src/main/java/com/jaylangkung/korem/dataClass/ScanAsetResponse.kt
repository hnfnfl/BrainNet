package com.jaylangkung.korem.dataClass

data class ScanAsetResponse(
    val data: List<ScanAsetData>,
    val status: String,
    val message: String,
)