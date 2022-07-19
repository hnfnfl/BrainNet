package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.menu_pelanggan.restart.UserEntity

class UserResponse(
    var status: String = "",
    var data: ArrayList<UserEntity>,
)