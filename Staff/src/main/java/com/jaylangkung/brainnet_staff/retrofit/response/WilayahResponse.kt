package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.menu_pelanggan.spinnerData.WilayahEntity

class WilayahResponse(
    var provinsi: ArrayList<WilayahEntity>,
    var kota_kabupaten: ArrayList<WilayahEntity>,
    var kecamatan: ArrayList<WilayahEntity>,
    var kelurahan: ArrayList<WilayahEntity>,
)