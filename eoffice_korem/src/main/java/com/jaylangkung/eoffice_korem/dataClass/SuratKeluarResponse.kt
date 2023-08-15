package com.jaylangkung.eoffice_korem.dataClass

data class SuratKeluarResponse(
    val data: ArrayList<SuratKeluarData>,
    val status: String
)

data class SuratKeluarData(
    val idsuratKeluar: String,
    val img: ArrayList<SuratImg>?,
    val nomerAgenda: String,
    val penerima: String,
    val perihal: String,
    val pdf: ArrayList<SuratPdf>?,
    val riwayat: String,
    val riwayatDisposisi: ArrayList<SuratRiwayatDisposisi>,
    val statusSuratKeluar: String,
    val tandaTangan: String,
    val tanggalSurat: String
)