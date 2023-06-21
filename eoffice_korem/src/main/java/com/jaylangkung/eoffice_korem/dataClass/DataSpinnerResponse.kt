package com.jaylangkung.eoffice_korem.dataClass

data class DataSpinnerResponse(
    val departemen: ArrayList<SpinnerDepartemenData>,
    val user_surat: ArrayList<UserSuratSpinnerData>
)

data class UserSuratSpinnerData(
    val idsurat_user_aktivasi: String,
    val nama: String,
    val nrp: String,
    val satuan: String
)

data class SpinnerDepartemenData(
    val departemen: String,
    val iddepartemen: String
)