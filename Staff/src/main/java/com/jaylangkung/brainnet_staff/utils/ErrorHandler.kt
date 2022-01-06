package com.jaylangkung.brainnet_staff.utils

import android.content.Context
import android.util.Log
import com.jaylangkung.brainnet_staff.R
import es.dmoral.toasty.Toasty
import java.util.*

class ErrorHandler {
    fun responseHandler(context: Context, func: String, message: String) {
        val now: Date = Calendar.getInstance().time
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
        Log.e("Logger", "context : $context, fun : $func, message : $message")
    }
}