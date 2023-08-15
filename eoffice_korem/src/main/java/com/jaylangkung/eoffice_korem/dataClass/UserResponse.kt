package com.jaylangkung.eoffice_korem.dataClass

data class UserResponse(
    val data: ArrayList<UserData>,
    val message: String,
    val status: String,
    val tokenAuth: String
)

data class UserData(
    val aksesSurat: Boolean,
    val corps: String,
    val iduserAktivasi: String,
    val iduserSurat: String,
    val img: String,
    val jabatan: String,
    val nama: String,
    val notelp: String,
    val pangkat: String,
    val pangkatJabatan: String,
    val tanggalLahir: String,
    val tmtJabatan: String,
    val username: String
)