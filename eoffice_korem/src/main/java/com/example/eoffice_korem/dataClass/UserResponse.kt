package com.example.eoffice_korem.dataClass

data class UserResponse(
    val data: ArrayList<UserData>,
    val message: String,
    val status: String,
    val tokenAuth: String
)

data class UserData(
    val akses_surat: Boolean,
    val corps: String,
    val iduser_aktivasi: String,
    val iduser_surat: String,
    val img: String,
    val jabatan: String,
    val nama: String,
    val notelp: String,
    val pangkat: String,
    val pangkat_jabatan: String,
    val tanggal_lahir: String,
    val tmt_jabatan: String,
    val username: String
)