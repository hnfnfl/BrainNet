package com.jaylangkung.brainnet_staff

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jaylangkung.brainnet_staff.databinding.ActivityMainBinding
import com.jaylangkung.brainnet_staff.scanner.ScannerActivity
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        myPreferences = MySharedPreferences(this@MainActivity)

        val nama = myPreferences.getValue(Constants.USER_NAMA)

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mainBinding.tvGreetings.text = when (currentHour) {
            in 4..11 -> getString(R.string.greetings, "Selamat Pagi", nama)
            in 12..14 -> getString(R.string.greetings, "Selamat Siang", nama)
            in 15..17 -> getString(R.string.greetings, "Selamat Sore", nama)
            else -> getString(R.string.greetings, "Selamat Malam", nama)
        }

        mainBinding.llScanner.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScannerActivity::class.java))
        }
    }
}