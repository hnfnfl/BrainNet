package com.jaylangkung.brainnet_staff.data_class

class TiangResponse(
    var status: String = "",
    var data: ArrayList<TiangEntity>
)

class TiangEntity(
    var idtiang: String = "",
    var serial_number: String = "",
    var geometry: String = "",
    var keterangan: String = ""
)