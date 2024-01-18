package com.jaylangkung.brainnet_staff.data_class

class GangguanResponse(
    var status: String = "",
    var data: ArrayList<GangguanEntity>
)

class GangguanEntity(
    var idgangguan: String = "",
    var nomer_tiket: String = "",
    var idpelanggan: String = "",
    var nama: String = "",
    var alamat: String = "",
    var kepada: String = "",
    var status: String = "",
    var prioritas: String = "",
    var isi: String = "",
    var tanggal: String = "",
)