package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.auth.StaffEntity

class LoginResponse(
    var status: String = "",
    var message: String = "",
    var data: ArrayList<StaffEntity>,
    var tokenAuth: String = ""
)

