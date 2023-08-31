package com.jaylangkung.eoffice_korem.dataClass

import com.google.gson.annotations.SerializedName

data class NotifikasiResponse(
    val data: ArrayList<NotifikasiData>,
    val status: String
)

data class NotifikasiData(
    val judul: String,
    @SerializedName("notifikasi_user") val notifikasiUser: String,
    val waktu: String
)