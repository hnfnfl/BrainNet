package com.jaylangkung.korem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.jaylangkung.korem.databinding.ActivityPresensiBinding
import com.jaylangkung.korem.utils.MySharedPreferences

class PresensiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPresensiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresensiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@PresensiActivity)

        codeScanner = CodeScanner(this@PresensiActivity, binding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.startPreview()

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                binding.loadingAnim.visibility = View.VISIBLE
//                getAbsensi(it.text, idadmin, tokenAuth)
            }
        }
        codeScanner.errorCallback = ErrorCallback.SUPPRESS

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@PresensiActivity, MainActivity::class.java))
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}