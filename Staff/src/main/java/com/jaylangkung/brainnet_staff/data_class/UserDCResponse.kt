package com.jaylangkung.brainnet_staff.data_class

class UserDCResponse(
    var status: String = "",
    var data: ArrayList<UserDCEntity>,
)

class UserDCEntity(
    var nama: String = "",
    var user: String = "",
    var paket: String = "",
    var nohp: String = "",
    var alamat_pasang: String = "",
    var jenis_kabel: String = "",
    var panjangkabel: String = "",
    var nomer_switch: String = ""
)