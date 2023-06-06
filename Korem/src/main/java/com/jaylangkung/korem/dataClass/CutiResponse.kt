package com.jaylangkung.korem.dataClass

data class CutiResponse(
    val data: ArrayList<CutiData>,
    val status: String
)

data class CutiData(
    val alasan_ditolak: String,
    val cuti: String,
    val jenis: String,
    val keterangan: String,
    val mulai: String,
    val proses: String,
    val revisi: String,
    val sampai: String,
    val status_cuti: String
)