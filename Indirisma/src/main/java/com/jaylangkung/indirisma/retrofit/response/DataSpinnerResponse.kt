package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.DataSpinnerEntity

class DataSpinnerResponse(
    var paketInternet: ArrayList<DataSpinnerEntity>,
    var marketing: ArrayList<DataSpinnerEntity>,
    var rekanan: ArrayList<DataSpinnerEntity>,
    var pelanggan: ArrayList<DataSpinnerEntity>,
    var belumAktif: ArrayList<DataSpinnerEntity>,
    var switch: ArrayList<DataSpinnerEntity>,
    var paketInstalasi: ArrayList<DataSpinnerEntity>,
    var tagihan: ArrayList<DataSpinnerEntity>,
)