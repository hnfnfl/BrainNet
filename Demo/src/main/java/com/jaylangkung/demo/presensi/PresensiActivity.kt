package com.jaylangkung.demo.presensi

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.jaylangkung.demo.MainActivity
import com.jaylangkung.demo.R
import com.jaylangkung.demo.databinding.ActivityPresensiBinding
import com.jaylangkung.demo.retrofit.DataService
import com.jaylangkung.demo.retrofit.RetrofitClient
import com.jaylangkung.demo.retrofit.response.DefaultResponse
import com.jaylangkung.demo.utils.Constants
import com.jaylangkung.demo.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresensiActivity : AppCompatActivity() {

    private lateinit var presensiBinding: ActivityPresensiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presensiBinding = ActivityPresensiBinding.inflate(layoutInflater)
        setContentView(presensiBinding.root)
        myPreferences = MySharedPreferences(this@PresensiActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        codeScanner = CodeScanner(this@PresensiActivity, presensiBinding.scannerView)
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
                presensiBinding.loadingAnim.visibility = View.VISIBLE
                getAbsensi(it.text, idadmin, tokenAuth)
            }
        }
        codeScanner.errorCallback = ErrorCallback.SUPPRESS

        presensiBinding.btnBack.setOnClickListener {
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
        startActivity(Intent(this@PresensiActivity, MainActivity::class.java))
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
        service.getAbsensi(token, idadmin, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                presensiBinding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    when (response.body()!!.status) {
                        "success" -> {
                            Toasty.success(this@PresensiActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        "already" -> {
                            Toasty.warning(this@PresensiActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        "not_match" -> {
                            Toasty.warning(this@PresensiActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            onBackPressed()
                        }
                        else -> {
                            Toasty.error(this@PresensiActivity, response.message(), Toasty.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toasty.error(this@PresensiActivity, response.message(), Toasty.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                presensiBinding.loadingAnim.visibility = View.GONE
                Toasty.error(this@PresensiActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }
}