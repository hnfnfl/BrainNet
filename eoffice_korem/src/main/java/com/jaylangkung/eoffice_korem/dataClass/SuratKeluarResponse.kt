package com.jaylangkung.eoffice_korem.dataClass

import com.google.gson.annotations.SerializedName

data class SuratKeluarResponse(
    val data: ArrayList<SuratKeluarData>, val status: String
)

data class SuratKeluarData(
    @SerializedName("idsurat_keluar") val idsuratKeluar: String,
    val img: ArrayList<SuratImg>?,
    @SerializedName("nomer_agenda") val nomerAgenda: String,
    val penerima: String,
    val perihal: String,
    val pdf: ArrayList<SuratPdf>?,
    val riwayat: String,
    @SerializedName("riwayat_disposisi") val riwayatDisposisi: ArrayList<SuratRiwayatDisposisi>,
    @SerializedName("status_surat_keluar") val statusSuratKeluar: String,
    @SerializedName("tanda_tangan") val tandaTangan: String,
    @SerializedName("tanggal_surat") val tanggalSurat: String
)