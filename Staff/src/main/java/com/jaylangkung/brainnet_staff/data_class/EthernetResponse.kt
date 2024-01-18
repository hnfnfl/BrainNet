package com.jaylangkung.brainnet_staff.data_class

class EthernetResponse(
    var status: String = "",
    var data: ArrayList<EthernetEntity>,
)

class EthernetEntity(
    var name: String = "",
    var status: String = ""
)