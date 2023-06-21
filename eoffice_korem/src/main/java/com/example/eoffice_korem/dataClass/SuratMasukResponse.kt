package com.example.eoffice_korem.dataClass

data class SuratMasukResponse(
    val data: ArrayList<SuratMasukData>,
    val status: String
)

data class SuratMasukData(
    val bentuk: String,
    val idsurat_masuk: String,
    val img: ArrayList<SuratMasukImg>?,
    val nomer_agenda: String,
    val penerima: String,
    val riwayat: String,
    val riwayat_disposisi: ArrayList<SuratRiwayatDisposisi>,
    val status_surat: String,
    val sumber: String,
    val tanggal_surat: String
)

data class SuratMasukImg(
    val id: Int,
    val img: String
)