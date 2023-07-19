package com.jaylangkung.brainnet_staff.data_class

class NotifikasiResponse(
    var status: String = "",
    var data: ArrayList<NotifikasiEntity>
)

class NotifikasiEntity(
    var jenis: String = "",
    var waktu: String = "",
    var isi: String = "",
)