package com.jaylangkung.brainnet_staff.tiang

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityScannerTiangBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.TiangResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerTiangActivity : AppCompatActivity() {

    private lateinit var scannerTiangBinding: ActivityScannerTiangBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerTiangBinding = ActivityScannerTiangBinding.inflate(layoutInflater)
        setContentView(scannerTiangBinding.root)
        myPreferences = MySharedPreferences(this@ScannerTiangActivity)

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        if (ContextCompat.checkSelfPermission(this@ScannerTiangActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@ScannerTiangActivity, arrayOf(Manifest.permission.CAMERA), 100
            )
        }
        codeScanner = CodeScanner(this@ScannerTiangActivity, scannerTiangBinding.scannerView)
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
                scannerTiangBinding.loadingAnim.visibility = View.VISIBLE
                getTiang(it.text, tokenAuth)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {}
        }

        scannerTiangBinding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@ScannerTiangActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ScannerTiangActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                onBackPressed()
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
        startActivity(Intent(this@ScannerTiangActivity, MainActivity::class.java))
        finish()
    }

    private fun getTiang(idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getTiang(idadmin, tokenAuth).enqueue(object : Callback<TiangResponse> {
            override fun onResponse(call: Call<TiangResponse>, response: Response<TiangResponse>) {
                scannerTiangBinding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        val intent = Intent(this@ScannerTiangActivity, EditTiangActivity::class.java)
                            .apply {
                                putExtra(EditTiangActivity.idtiang, response.body()!!.data[0].idtiang)
                                putExtra(EditTiangActivity.serialNumber, response.body()!!.data[0].serial_number)
                            }
                        startActivity(intent)
                        finish()
                    } else if (response.body()!!.status == "empty") {
                        Toasty.warning(this@ScannerTiangActivity, "Nomor Seri tiang tidak ditemukan", Toast.LENGTH_LONG).show()
                        onBackPressed()
                    }
                }
            }

            override fun onFailure(call: Call<TiangResponse>, t: Throwable) {
                scannerTiangBinding.loadingAnim.visibility = View.GONE
                Toasty.error(this@ScannerTiangActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }
}