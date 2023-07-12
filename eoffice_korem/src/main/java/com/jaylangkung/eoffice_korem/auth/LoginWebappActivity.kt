package com.jaylangkung.eoffice_korem.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.jaylangkung.eoffice_korem.MainActivity
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityLoginWebappBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginWebappActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWebappBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWebappBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@LoginWebappActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@LoginWebappActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        codeScanner = CodeScanner(this@LoginWebappActivity, binding.scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.CONTINUOUS
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            startPreview()

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.loadingAnim.visibility = View.VISIBLE
                    if (it.text.contains("webapp", ignoreCase = true)) {
                        insertWebApp(iduser, it.text, tokenAuth)
                    } else {
                        Toasty.warning(this@LoginWebappActivity, "QR Code tidak cocok", Toasty.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    ErrorHandler().responseHandler(
                        this@LoginWebappActivity,
                        "codeScanner | errorCallback", it.message.toString()
                    )
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
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    private fun insertWebApp(idadmin: String, device_id: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.insertWebApp(idadmin, device_id, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@LoginWebappActivity, "Login berhasil", Toasty.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toasty.error(this@LoginWebappActivity, "Login gagal", Toasty.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@LoginWebappActivity,
                        "insertWebApp | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@LoginWebappActivity,
                    "insertWebApp | onResponse", t.message.toString()
                )
            }
        })
    }
}