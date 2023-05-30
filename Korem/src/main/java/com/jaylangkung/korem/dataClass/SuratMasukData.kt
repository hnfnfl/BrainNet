package com.jaylangkung.korem.dataClass

data class SuratMasukData(
    val bentuk: String,
    val idsurat_masuk: String,
    val nomer_agenda: String,
    val penerima: String,
    val riwayat: String,
    val riwayat_disposisi: ArrayList<SuratRiwayatDisposisi>,
    val status_surat: String,
    val sumber: String,
    val tanggal_surat: String
)