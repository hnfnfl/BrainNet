package com.jaylangkung.brainnet_staff.scanner

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
import com.jaylangkung.brainnet_staff.data_class.TiangResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityScannerBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.tiang.EditTiangActivity
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
        val caller = intent.getStringExtra("caller").toString()

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            when (caller) {
                "presensi" -> {
                    tvTitle.text = getString(R.string.presensi)
                }

                "webapp" -> {
                    tvTitle.text = getString(R.string.login_webapp)
                }

                "tiang" -> {
                    tvTitle.text = getString(R.string.edit_data_tiang)
                }
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
                        when (caller) {
                            "presensi" -> {
                                getAbsensi(it.text, idadmin, tokenAuth)
                            }

                            "webapp" -> {
                                if (it.text.contains("webapp", ignoreCase = true)) {
                                    insertWebApp(idadmin, it.text, tokenAuth)
                                } else {
                                    Toasty.warning(this@ScannerActivity, "QR Code tidak valid", Toasty.LENGTH_SHORT, true).show()
                                    vibrate()
                                    startPreview()
                                }
                            }

                            "tiang" -> {
                                getTiang(idadmin, tokenAuth)
                            }
                        }
                    }
                }

                errorCallback = ErrorCallback {
                    runOnUiThread {
                        ErrorHandler().responseHandler(
                            this@ScannerActivity, "codeScanner | errorCallback", it.message.toString()
                        )
                        Toasty.error(this@ScannerActivity, it.message.toString(), Toasty.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
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

    private fun insertWebApp(idadmin: String, device_id: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertWebApp(idadmin, device_id, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@ScannerActivity, "Login berhasil", Toasty.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@ScannerActivity, "insertWebApp | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@ScannerActivity, "insertWebApp | onResponse", t.message.toString()
                )
            }
        })
    }

    private fun getTiang(idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getTiang(idadmin, tokenAuth).enqueue(object : Callback<TiangResponse> {
            override fun onResponse(call: Call<TiangResponse>, response: Response<TiangResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    if (response.body()!!.status == "success") {
                        val intent = Intent(this@ScannerActivity, EditTiangActivity::class.java).apply {
                            putExtra(EditTiangActivity.idtiang, response.body()!!.data[0].idtiang)
                            putExtra(EditTiangActivity.serialNumber, response.body()!!.data[0].serial_number)
                        }
                        startActivity(intent)
                        finish()
                    } else if (response.body()!!.status == "empty") {
                        Toasty.warning(this@ScannerActivity, "Nomor Seri tiang tidak ditemukan", Toast.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    onBackPressedDispatcher.onBackPressed()
                    ErrorHandler().responseHandler(
                        this@ScannerActivity, "getTiang | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<TiangResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@ScannerActivity, "getTiang | onFailure", t.message.toString()
                )
            }
        })
    }
}