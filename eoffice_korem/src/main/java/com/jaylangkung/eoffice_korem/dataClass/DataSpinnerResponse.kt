package com.jaylangkung.eoffice_korem.dataClass

data class DataSpinnerResponse(
    val user_surat: ArrayList<UserSuratSpinnerData>
)

data class UserSuratSpinnerData(
    val idsurat_user_aktivasi: String,
    val nama: String,
    val nrp: String,
    val satuan: String
)
