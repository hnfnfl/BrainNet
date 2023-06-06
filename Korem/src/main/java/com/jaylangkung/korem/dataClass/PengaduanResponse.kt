package com.jaylangkung.korem.dataClass

data class PengaduanResponse(
    val data: ArrayList<PengaduanData>,
    val status: String
)

data class PengaduanData(
    val balasan: String,
    val createddate: String,
    val iduser_aktivasi: String,
    val iduser_pengaduan: String,
    val img: String,
    val judul: String,
    val lastupdate: String,
    val oleh: String,
    val pengaduan: String,
    val status_pengaduan: String
)