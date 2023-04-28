package com.jaylangkung.korem.dataClass

data class GiatData(
    val departemen: String,
    val img: ArrayList<GiatImg>?,
    val jenis: String,
    val keterangan: String,
    val lokasi: String,
    val mulai: String,
    val posisi_giat: ArrayList<GiatPosisiGiat>,
    val proses: String,
    val sampai: String,
    val tujuan: String
)

data class GiatPosisiGiat(
    val lat: String,
    val lng: String
)

data class GiatImg(
    val id: Int,
    val img: String
)