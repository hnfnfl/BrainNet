package com.jaylangkung.eoffice_korem.dataClass

data class NotifikasiResponse(
    val data: ArrayList<NotifikasiData>,
    val status: String
)

data class NotifikasiData(
    val judul: String,
    val notifikasi_user: String,
    val waktu: String
)