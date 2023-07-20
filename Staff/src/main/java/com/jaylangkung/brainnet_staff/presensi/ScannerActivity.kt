package com.jaylangkung.brainnet_staff.presensi

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.DefaultResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityScannerBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ScannerActivity, MainActivity::class.java))
                finish()
            }
        })

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            codeScanner = CodeScanner(this@ScannerActivity, scannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = CodeScanner.ALL_FORMATS
                autoFocusMode = AutoFocusMode.CONTINUOUS
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false
                startPreview()

                decodeCallback = DecodeCallback {
                    runOnUiThread {
                        loadingAnim.visibility = View.VISIBLE
                        getAbsensi(it.text, idadmin, tokenAuth)
                    }
                }

                errorCallback = ErrorCallback {
                    runOnUiThread {
                        ErrorHandler().responseHandler(
                            this@ScannerActivity, "codeScanner | errorCallback", it.message.toString()
                        )
                    }
                }
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

    private fun vibrate() {
        val vibrator = ContextCompat.getSystemService(this, Vibrator::class.java) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION") vibrator.vibrate(200)
        }
    }

    private fun getAbsensi(token: String, idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getAbsensi(token, idadmin, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    when (response.body()!!.status) {
                        "success" -> {
                            Toasty.success(this@ScannerActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                        }

                        "already" -> {
                            Toasty.warning(this@ScannerActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                        }

                        "not_match" -> {
                            Toasty.warning(this@ScannerActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Toasty.error(this@ScannerActivity, response.message(), Toasty.LENGTH_LONG).show()
                        }
                    }
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    ErrorHandler().responseHandler(
                        this@ScannerActivity, "getAbsensi | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@ScannerActivity, "getAbsensi | onFailure", t.message.toString()
                )
            }
        })
    }
}