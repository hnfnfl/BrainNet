package com.jaylangkung.indirisma.utils.room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.databinding.ActivityLoggerBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoggerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoggerBinding
    private lateinit var loggerDatabase: LoggerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loggerDatabase = Room.databaseBuilder(this@LoggerActivity, LoggerDatabase::class.java, "logger.db").build()

        binding.btnBack.setOnClickListener { onBackPressed() }

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
            binding.tvDisplay.text = displayText
        }
    }
}