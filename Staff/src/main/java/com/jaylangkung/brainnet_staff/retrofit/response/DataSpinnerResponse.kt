package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.pelanggan.spinnerData.DataSpinnerEntity

class DataSpinnerResponse(
    var paketInternet: ArrayList<DataSpinnerEntity>,
    var marketing: ArrayList<DataSpinnerEntity>,
    var rekanan: ArrayList<DataSpinnerEntity>,
    var pelanggan: ArrayList<DataSpinnerEntity>,
    var switch: ArrayList<DataSpinnerEntity>,
    var paketInstalasi: ArrayList<DataSpinnerEntity>,
    var tagihan: ArrayList<DataSpinnerEntity>,
)