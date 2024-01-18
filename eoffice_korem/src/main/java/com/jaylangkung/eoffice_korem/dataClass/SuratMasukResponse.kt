package com.jaylangkung.eoffice_korem.dataClass

import com.google.gson.annotations.SerializedName

data class SuratMasukResponse(
    val data: ArrayList<SuratMasukData>, val status: String
)

data class SuratMasukData(
    val bentuk: String,
    @SerializedName("idsurat_masuk") val idsuratMasuk: String,
    val img: ArrayList<SuratImg>?,
    @SerializedName("nomer_agenda") val nomerAgenda: String,
    val penerima: String,
    val riwayat: String,
    @SerializedName("riwayat_disposisi") val riwayatDisposisi: ArrayList<SuratRiwayatDisposisi>,
    @SerializedName("status_surat") val statusSurat: String,
    val sumber: String,
    @SerializedName("tanggal_surat") val tanggalSurat: String
)

data class SuratImg(
    val id: Int, val img: String
)

data class SuratPdf(
    val id: Int, val pdf: String
)