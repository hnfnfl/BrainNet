package com.jaylangkung.eoffice_korem.dataClass

data class SuratMasukResponse(
    val data: ArrayList<SuratMasukData>,
    val status: String
)

data class SuratMasukData(
    val bentuk: String,
    val idsuratMasuk: String,
    val img: ArrayList<SuratImg>?,
    val nomerAgenda: String,
    val penerima: String,
    val riwayat: String,
    val riwayatDisposisi: ArrayList<SuratRiwayatDisposisi>,
    val statusSurat: String,
    val sumber: String,
    val tanggalSurat: String
)

data class SuratImg(
    val id: Int,
    val img: String
)

data class SuratPdf(
    val id: Int,
    val pdf: String
)