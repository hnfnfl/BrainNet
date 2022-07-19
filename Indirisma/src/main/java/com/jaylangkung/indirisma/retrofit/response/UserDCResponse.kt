package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.monitoring.UserDCEntity

class UserDCResponse(
    var status: String = "",
    var data: ArrayList<UserDCEntity>,
)