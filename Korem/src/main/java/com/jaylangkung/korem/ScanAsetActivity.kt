package com.jaylangkung.korem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.jaylangkung.korem.dataClass.ScanAsetResponse
import com.jaylangkung.korem.databinding.ActivityScanAsetBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanAsetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanAsetBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanAsetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@ScanAsetActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ScanAsetActivity, MainActivity::class.java))
                finish()
            }
        })

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        codeScanner = CodeScanner(this@ScanAsetActivity, binding.scannerView)
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
                binding.loadingAnim.visibility = View.VISIBLE
                getAset(it.text, tokenAuth)
            }
        }
        codeScanner.errorCallback = ErrorCallback.SUPPRESS

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    private fun getAset(kode: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getAset(kode, tokenAuth).enqueue(object : Callback<ScanAsetResponse> {
            override fun onResponse(call: Call<ScanAsetResponse>, response: Response<ScanAsetResponse>) {
                binding.loadingAnim.visibility = View.GONE
                if (response.isSuccessful) {
                    vibrate()
                    when (response.body()!!.status) {
                        "success" -> {
                            val data = response.body()!!.data[0]
                            val kodeAset = data.kode
                            val nama = data.nama
                            val jenis = data.jenis
                            val dept = data.departemen
                            val mDialog = MaterialDialog.Builder(this@ScanAsetActivity as Activity)
                                .setTitle("SuratMasukData Aset Kode: $kodeAset")
                                .setMessage("Nama Aset: $nama Jenis: $jenis SpinnerDepartemenData: $dept")
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.yes))
                                { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    codeScanner.startPreview()
                                }
                                .build()
                            // Show Dialog
                            mDialog.show()
                        }
                        "empty" -> {
                            Toasty.error(this@ScanAsetActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                            codeScanner.startPreview()
                        }
                        else -> {
                            Toasty.error(this@ScanAsetActivity, response.message(), Toasty.LENGTH_LONG).show()
                            codeScanner.startPreview()
                        }
                    }
                } else {
                    codeScanner.startPreview()
                    ErrorHandler().responseHandler(
                        this@ScanAsetActivity,
                        "getAset | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<ScanAsetResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                codeScanner.startPreview()
                ErrorHandler().responseHandler(
                    this@ScanAsetActivity,
                    "getAset | onFailure", t.message.toString()
                )
            }
        })
    }
}