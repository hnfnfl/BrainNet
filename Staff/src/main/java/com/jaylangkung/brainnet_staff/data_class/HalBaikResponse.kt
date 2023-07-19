package com.jaylangkung.brainnet_staff.data_class

class HalBaikResponse(
    var status: String = "",
    var data: ArrayList<HalBaikEntity>
)

class HalBaikEntity(
    var idhal_baik: String = "",
    var hal_baik: String = "",
    var idadmin: String = "",
    var tanggal: String = ""
)