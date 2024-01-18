package com.jaylangkung.eoffice_korem.dataClass

import com.google.gson.annotations.SerializedName

data class SuratRiwayatDisposisi(
    val aksi: String,
    val balasan: String?,
    val catatan: String,
    @SerializedName("catatan_tambahan") val catatanTambahan: String,
    @SerializedName("nomer_agenda") val nomerAgenda: String,
    val penerima: String,
    val pengirim: String,
    @SerializedName("tanggal_disposisi") val tanggalDisposisi: String
)