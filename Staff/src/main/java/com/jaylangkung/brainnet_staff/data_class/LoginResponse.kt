package com.jaylangkung.brainnet_staff.data_class

class LoginResponse(
    var status: String = "",
    var message: String = "",
    var data: ArrayList<StaffEntity>,
    var tokenAuth: String = ""
)

class StaffEntity(
    var idadmin: String = "",
    var email: String = "",
    var nama: String = "",
    var alamat: String = "",
    var telp: String = "",
    var img: String = "",
    var device_token: String = "",
    var idlevel: String = "",
    var judul: String = "",
)