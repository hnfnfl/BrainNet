package com.jaylangkung.korem.dataClass

data class ScanAsetResponse(
    val data: ArrayList<ScanAsetData>,
    val status: String,
    val message: String,
)

data class ScanAsetData(
    val departemen: String,
    val jenis: String,
    val kode: String,
    val nama: String
)