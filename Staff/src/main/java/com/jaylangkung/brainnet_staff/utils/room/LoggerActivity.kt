package com.jaylangkung.brainnet_staff.utils.room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityLoggerBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoggerActivity : AppCompatActivity() {

    private lateinit var loggerBinding: ActivityLoggerBinding
    private lateinit var loggerDatabase: LoggerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loggerBinding = ActivityLoggerBinding.inflate(layoutInflater)
        setContentView(loggerBinding.root)
        loggerDatabase = Room.databaseBuilder(this@LoggerActivity, LoggerDatabase::class.java, "logger.db").build()

        loggerBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        GlobalScope.launch {
            displayData()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@LoggerActivity, MainActivity::class.java))
        finish()
    }

    private fun displayData() {
        val data: List<Logger> = loggerDatabase.loggerDao().getAllLog()
        var displayText = ""
        for (key in data) {
            displayText += "\nTime : ${key.time}\nContext : ${key.context}\nFun : ${key.func}\nMessage : ${key.message}\n"
        }
        runOnUiThread {
            loggerBinding.tvDisplay.text = displayText
        }
    }
}