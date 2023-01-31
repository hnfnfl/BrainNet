package com.jaylangkung.korem.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jaylangkung.korem.databinding.ActivityLoginBinding
import com.jaylangkung.korem.utils.MySharedPreferences

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}