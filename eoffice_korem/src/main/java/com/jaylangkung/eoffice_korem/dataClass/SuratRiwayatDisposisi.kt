package com.jaylangkung.eoffice_korem.dataClass

data class SuratRiwayatDisposisi(
    val aksi: String,
    val balasan: String?,
    val catatan: String,
    val catatanTambahan: String,
    val nomerAgenda: String,
    val penerima: String,
    val pengirim: String,
    val tanggalDisposisi: String
)