package com.jaylangkung.indirisma.presensi

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
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityScannerBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@ScannerActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        codeScanner = CodeScanner(this@ScannerActivity, binding.scannerView).apply {
            // Parameters (default values)
            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
            autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
            scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not
            startPreview()

            // Callbacks
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.loadingAnim.visibility = View.VISIBLE
                    getAbsensi(it.text, idadmin, tokenAuth)
                }
            }
            errorCallback = ErrorCallback.SUPPRESS
        }

        binding.btnBack.setOnClickListener { onBackPressed() }
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

    private fun vibrate() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    private fun getAbsensi(token: String, idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getAbsensi(token, idadmin, tokenAuth, "true").enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    when (response.body()!!.status) {
                        "success" -> {
                            Toasty.success(this@ScannerActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        "already" -> {
                            Toasty.warning(this@ScannerActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        "not_match" -> {
                            Toasty.warning(this@ScannerActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        else -> {
                            Toasty.error(this@ScannerActivity, response.message(), Toasty.LENGTH_LONG).show()
                        }
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@ScannerActivity,
                        "getAbsensi | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@ScannerActivity,
                    "getAbsensi | onFailure", t.message.toString()
                )
            }
        })
    }
}