package com.jaylangkung.korem.dataClass

data class SuratKeluarData(
    val idsurat_keluar: String,
    val nomer_agenda: String,
    val penerima: String,
    val perihal: String,
    val riwayat: String,
    val riwayat_disposisi: ArrayList<SuratRiwayatDisposisi>,
    val status_surat_keluar: String,
    val tanggal_surat: String
)