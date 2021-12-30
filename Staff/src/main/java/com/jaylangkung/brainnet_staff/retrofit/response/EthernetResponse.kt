package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.monitoring.EthernetEntity

class EthernetResponse(
    var status: String = "",
    var data: ArrayList<EthernetEntity>,
)