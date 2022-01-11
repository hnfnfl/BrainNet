package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.pelanggan.spinnerData.DataSpinnerEntity

class AddCustomerSpinnerResponse(
    var paketInternet: ArrayList<DataSpinnerEntity>,
    var marketing: ArrayList<DataSpinnerEntity>,
    var rekanan: ArrayList<DataSpinnerEntity>,
)