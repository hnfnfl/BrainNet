package com.jaylangkung.demo.retrofit.response

import com.jaylangkung.demo.auth.StaffEntity

class LoginResponse(
    var status: String = "",
    var message: String = "",
    var data: ArrayList<StaffEntity>,
    var tokenAuth: String = ""
)

