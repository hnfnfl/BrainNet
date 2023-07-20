package com.jaylangkung.brainnet_staff.tiang

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
import com.jaylangkung.brainnet_staff.data_class.TiangResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityScannerTiangBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ScannerTiangActivity, MainActivity::class.java))
                finish()
            }
        })

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            codeScanner = CodeScanner(this@ScannerTiangActivity, scannerView).apply {
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
                        getTiang(it.text, tokenAuth)
                    }
                }

                errorCallback = ErrorCallback {
                    runOnUiThread {
                        Toasty.error(this@ScannerTiangActivity, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
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

    private fun getTiang(idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getTiang(idadmin, tokenAuth).enqueue(object : Callback<TiangResponse> {
            override fun onResponse(call: Call<TiangResponse>, response: Response<TiangResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    if (response.body()!!.status == "success") {
                        val intent = Intent(this@ScannerTiangActivity, EditTiangActivity::class.java).apply {
                            putExtra(EditTiangActivity.idtiang, response.body()!!.data[0].idtiang)
                            putExtra(EditTiangActivity.serialNumber, response.body()!!.data[0].serial_number)
                        }
                        startActivity(intent)
                        finish()
                    } else if (response.body()!!.status == "empty") {
                        Toasty.warning(this@ScannerTiangActivity, "Nomor Seri tiang tidak ditemukan", Toast.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    onBackPressedDispatcher.onBackPressed()
                    ErrorHandler().responseHandler(
                        this@ScannerTiangActivity, "getTiang | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<TiangResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@ScannerTiangActivity, "getTiang | onFailure", t.message.toString()
                )
            }
        })
    }
}