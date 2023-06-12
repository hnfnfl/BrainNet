package com.example.eoffice_korem.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.eoffice_korem.MainActivity2
import com.example.eoffice_korem.databinding.ActivityProfileBinding
import com.example.eoffice_korem.utils.Constants
import com.example.eoffice_korem.utils.MySharedPreferences
import com.example.eoffice_korem.auth.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@ProfileActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ProfileActivity, MainActivity2::class.java))
                finish()
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