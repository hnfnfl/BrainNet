package com.jaylangkung.korem.siapOpsGerak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.databinding.ActivitySiapOpsGerakBinding
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.MySharedPreferences

class SiapOpsGerakActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySiapOpsGerakBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiapOpsGerakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SiapOpsGerakActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SiapOpsGerakActivity, MainActivity::class.java))
                finish()
            }
        })

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }

        //TODO: add adapter for data
    }
}