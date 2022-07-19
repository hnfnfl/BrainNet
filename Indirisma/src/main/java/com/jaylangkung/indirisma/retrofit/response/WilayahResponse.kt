package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.WilayahEntity

class WilayahResponse(
    var provinsi: ArrayList<WilayahEntity>,
    var kota_kabupaten: ArrayList<WilayahEntity>,
    var kecamatan: ArrayList<WilayahEntity>,
    var kelurahan: ArrayList<WilayahEntity>,
)