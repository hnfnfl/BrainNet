package com.jaylangkung.eoffice_korem.dataClass

data class SuratRiwayatDisposisi(
    val aksi: String,
    val balasan: String?,
    val catatan: String,
    val catatan_tambahan: String,
    val nomer_agenda: String,
    val penerima: String,
    val pengirim: String,
    val tanggal_disposisi: String
)