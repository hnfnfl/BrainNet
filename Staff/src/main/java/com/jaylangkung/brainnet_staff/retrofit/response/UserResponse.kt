package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.menu_pelanggan.restart.UserEntity

class UserResponse(
    var status: String = "",
    var data: ArrayList<UserEntity>,
)