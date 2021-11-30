package com.jaylangkung.brainnet_staff.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityScannerBinding
import es.dmoral.toasty.Toasty

class ScannerActivity : AppCompatActivity() {

    private lateinit var scannerBinding: ActivityScannerBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerBinding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(scannerBinding.root)

        if (ContextCompat.checkSelfPermission(this@ScannerActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ScannerActivity, arrayOf(Manifest.permission.CAMERA), 100
            )
        }
        codeScanner = CodeScanner(this@ScannerActivity, scannerBinding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toasty.success(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {}
        }

        scannerBinding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@ScannerActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ScannerActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ScannerActivity, MainActivity::class.java))
        finish()
    }
}