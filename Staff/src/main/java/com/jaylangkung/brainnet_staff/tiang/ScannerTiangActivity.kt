package com.jaylangkung.brainnet_staff.tiang

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityScannerTiangBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.TiangResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerTiangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerTiangBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerTiangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@ScannerTiangActivity)

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        codeScanner = CodeScanner(this@ScannerTiangActivity, binding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.startPreview()

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                binding.loadingAnim.visibility = View.VISIBLE
                getTiang(it.text, tokenAuth)
            }
        }
        codeScanner.errorCallback = ErrorCallback.SUPPRESS

        binding.btnBack.setOnClickListener {
            onBackPressed()
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

    private fun vibrate() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    private fun getTiang(idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getTiang(idadmin, tokenAuth).enqueue(object : Callback<TiangResponse> {
            override fun onResponse(call: Call<TiangResponse>, response: Response<TiangResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
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
                } else {
                    onBackPressed()
                    ErrorHandler().responseHandler(
                        this@ScannerTiangActivity,
                        "getTiang | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<TiangResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@ScannerTiangActivity,
                    "getTiang | onFailure", t.message.toString()
                )
            }
        })
    }
}