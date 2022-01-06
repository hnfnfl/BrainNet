package com.jaylangkung.brainnet_staff.utils

import android.content.Context
import android.util.Log
import com.jaylangkung.brainnet_staff.R
import es.dmoral.toasty.Toasty

class ErrorHandler {
    fun responseHandler(context: Context, message: String) {
        when {
            message.contains("failed to connect to", ignoreCase = true) -> {
                Toasty.error(context, "Terdapat permasalahan pada server", Toasty.LENGTH_LONG).show()
            }
            message.contains("Unable to resolve host") -> {
                Toasty.error(context, "Silahkan cek koneksi internet Anda", Toasty.LENGTH_LONG).show()
            }
            else -> {
                Toasty.error(context, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        }
        Log.e("error message", message)
    }
}