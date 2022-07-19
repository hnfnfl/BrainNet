package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.monitoring.EthernetEntity

class EthernetResponse(
    var status: String = "",
    var data: ArrayList<EthernetEntity>,
)