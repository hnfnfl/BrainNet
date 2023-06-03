package com.jaylangkung.korem.dataClass

data class DataSpinnerResponse(
    val departemen: ArrayList<SpinnerDepartemenData>,
    val user_surat: ArrayList<UserSuratSpinnerData>
)