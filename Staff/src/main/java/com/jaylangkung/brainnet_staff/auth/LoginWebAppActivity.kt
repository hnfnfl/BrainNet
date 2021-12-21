package com.jaylangkung.brainnet_staff.auth

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
import com.jaylangkung.brainnet_staff.databinding.ActivityLoginWebAppBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginWebAppActivity : AppCompatActivity() {

    private lateinit var loginWebAppBinding: ActivityLoginWebAppBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginWebAppBinding = ActivityLoginWebAppBinding.inflate(layoutInflater)
        setContentView(loginWebAppBinding.root)
        myPreferences = MySharedPreferences(this@LoginWebAppActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        codeScanner = CodeScanner(this@LoginWebAppActivity, loginWebAppBinding.scannerView)
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
                loginWebAppBinding.loadingAnim.visibility = View.VISIBLE
                insertWebApp(idadmin, it.text, tokenAuth)
            }
        }
        codeScanner.errorCallback = ErrorCallback.SUPPRESS

        loginWebAppBinding.btnBack.setOnClickListener {
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
        startActivity(Intent(this@LoginWebAppActivity, MainActivity::class.java))
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

    private fun insertWebApp(idadmin: String, device_id: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertWebApp(idadmin, device_id, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                loginWebAppBinding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@LoginWebAppActivity, "Login berhasil", Toasty.LENGTH_LONG).show()
                        onBackPressed()
                    }
                } else {
                    Toasty.error(this@LoginWebAppActivity, response.message(), Toasty.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                loginWebAppBinding.loadingAnim.visibility = View.GONE
                Toasty.error(this@LoginWebAppActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }
}