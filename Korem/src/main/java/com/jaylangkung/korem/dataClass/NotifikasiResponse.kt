package com.jaylangkung.korem.dataClass

data class NotifikasiResponse(
    val data: ArrayList<NotifikasiData>,
    val status: String
)

data class NotifikasiData(
    val isi: String,
    val jenis: String,
    val waktu: String
)