package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.monitoring.UserDCEntity

class UserDCResponse(
    var status: String = "",
    var data: ArrayList<UserDCEntity>,
)