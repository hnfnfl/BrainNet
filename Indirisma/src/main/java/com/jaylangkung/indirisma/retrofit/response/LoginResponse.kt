package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.auth.StaffEntity

class LoginResponse(
    var status: String = "",
    var message: String = "",
    var data: ArrayList<StaffEntity>,
    var tokenAuth: String = ""
)

