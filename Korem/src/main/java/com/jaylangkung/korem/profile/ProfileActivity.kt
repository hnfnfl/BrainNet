package com.jaylangkung.korem.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.MainActivity2
import com.jaylangkung.korem.R
import com.jaylangkung.korem.auth.LoginActivity
import com.jaylangkung.korem.databinding.ActivityProfileBinding
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.MySharedPreferences

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@ProfileActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            val caller = intent.getStringExtra("caller")
            override fun handleOnBackPressed() {
                if (caller == "MainActivity") {
                    startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@ProfileActivity, MainActivity2::class.java))
                    finish()
                }
            }
        })

        binding.apply {
            tvNrp.isEnabled = false
            tvNrp.setText(myPreferences.getValue(Constants.USERNAME).toString())
            tvNama.isEnabled = false
            tvNama.setText(myPreferences.getValue(Constants.USER_NAMA).toString())
            tvPangkat.isEnabled = false
            tvPangkat.setText(myPreferences.getValue(Constants.USER_PANGKAT).toString())
            tvJabatan.isEnabled = false
            tvJabatan.setText(myPreferences.getValue(Constants.USER_JABATAN).toString())
            tvNotelp.isEnabled = false
            tvNotelp.setText(myPreferences.getValue(Constants.USER_TELP).toString())

            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnLogout.setOnClickListener {
                myPreferences.clear()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}