package com.jaylangkung.eoffice_korem.dataClass

data class SuratKeluarResponse(
    val data: ArrayList<SuratKeluarData>,
    val status: String
)

data class SuratKeluarData(
    val idsurat_keluar: String,
    val img: ArrayList<SuratImg>?,
    val nomer_agenda: String,
    val penerima: String,
    val perihal: String,
    val riwayat: String,
    val riwayat_disposisi: ArrayList<SuratRiwayatDisposisi>,
    val status_surat_keluar: String,
    val tanda_tangan: String,
    val tanggal_surat: String
)