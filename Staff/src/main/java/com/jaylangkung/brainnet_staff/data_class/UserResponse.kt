package com.jaylangkung.brainnet_staff.data_class

class UserResponse(
    var status: String = "",
    var data: ArrayList<UserEntity>,
)

class UserEntity(
    var user: String = "",
    var password: String = "",
    var nama: String = "",
    var paket: String = "",
    var alamat_pasang: String = ""
)