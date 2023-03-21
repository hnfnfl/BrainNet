package com.jaylangkung.korem.dataClass

data class SiapOpsGerakData(
    val createddate: String,
    val kendaraan: Kendaraan,
    val keterangan: String,
    val siap_gerak: String,
    val siap_ops: String
)

data class Kendaraan(
    val jenis: String,
    val jumlah: String
)