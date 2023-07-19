package com.jaylangkung.brainnet_staff.data_class

class WilayahResponse(
    var provinsi: ArrayList<WilayahEntity>,
    var kota_kabupaten: ArrayList<WilayahEntity>,
    var kecamatan: ArrayList<WilayahEntity>,
    var kelurahan: ArrayList<WilayahEntity>,
)

class WilayahEntity(
    var id: String = "",
    var nama: String = ""
)