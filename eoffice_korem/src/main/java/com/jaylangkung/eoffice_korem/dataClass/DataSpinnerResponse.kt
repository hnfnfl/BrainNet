package com.jaylangkung.eoffice_korem.dataClass

import com.google.gson.annotations.SerializedName

data class DataSpinnerResponse(
    @SerializedName("user_surat") val userSurat: ArrayList<UserSuratSpinnerData>
)

data class UserSuratSpinnerData(
    @SerializedName("idsurat_user_aktivasi") val idsuratUserAktivasi: String,
    val nama: String,
    val nrp: String,
    val satuan: String
)
