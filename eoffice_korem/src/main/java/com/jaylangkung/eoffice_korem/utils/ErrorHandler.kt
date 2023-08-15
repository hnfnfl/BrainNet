package com.jaylangkung.eoffice_korem.utils

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.utils.room.Logger
import com.jaylangkung.eoffice_korem.utils.room.LoggerDatabase
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ErrorHandler {

    private lateinit var loggerDatabase: LoggerDatabase

    fun responseHandler(context: Context, func: String, message: String) {
        val now: Date = Calendar.getInstance().time
        when {
            message.contains("failed to connect to", ignoreCase = true) -> {
                Toasty.error(context, "Terdapat permasalahan pada server", Toasty.LENGTH_LONG).show()
            }
            message.contains("Unable to resolve host") -> {
                Toasty.error(context, "Silahkan cek koneksi internet Anda", Toasty.LENGTH_LONG).show()
            }
            message.contains("Forbidden") -> {
                Toasty.error(context, "File yang Anda upload tidak didukung", Toasty.LENGTH_LONG).show()
            }
            else -> {
                Toasty.error(context, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        }

        GlobalScope.launch {
            insertDB(context, func, message, now.toString())
        }
        Log.e("Logger", "context : $context, fun : $func, message : $message, time : $now")
    }

    private fun insertDB(context: Context, func: String, message: String, time: String) {
        loggerDatabase = Room.databaseBuilder(context, LoggerDatabase::class.java, "logger.db").build()
        loggerDatabase.loggerDao().insert(
            Logger(context.toString(), func, message, time)
        )
    }
}