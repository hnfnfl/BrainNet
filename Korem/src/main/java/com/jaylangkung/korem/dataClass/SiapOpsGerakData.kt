package com.jaylangkung.korem.dataClass

data class SiapOpsGerakData(
    val createddate: String,
    val departemen: String,
    val kendaraan: ArrayList<SiapOpsGerakKendaraan>,
    val keterangan: String,
    val siap_gerak: String,
    val siap_ops: String
)