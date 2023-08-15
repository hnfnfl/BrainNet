package com.jaylangkung.eoffice_korem

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.eoffice_korem.auth.LoginActivity
import com.jaylangkung.eoffice_korem.dataClass.UserResponse
import com.jaylangkung.eoffice_korem.databinding.ActivitySplashScreenBinding
import com.jaylangkung.eoffice_korem.retrofit.AuthService
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.surat.keluar.SuratKeluarActivity
import com.jaylangkung.eoffice_korem.surat.masuk.SuratMasukActivity
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SplashScreen)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        binding.tvAppVersion.text = getString(R.string.app_version, versionName)
        val noAgenda = intent.getStringExtra("nomerAgenda").toString()
        val suratTipe = intent.getStringExtra("suratType").toString()

        binding.apply {
            if (myPreferences.getValue(Constants.firstTime) == "firstTime") {
                val videoUri = Uri.parse("android.resource://$packageName/${R.raw.splashscreen}")
                videoView.setVideoURI(videoUri)
                videoView.start()
            } else {
                videoView.visibility = View.GONE
                splashLayout.visibility = View.VISIBLE
            }

            Handler(Looper.getMainLooper()).postDelayed({
                //Stop video and show splash layout
                videoView.stopPlayback()
                videoView.visibility = View.GONE
                splashLayout.visibility = View.VISIBLE

                //Ketika user sudah login tidak perlu ke halaman login lagi
                if (myPreferences.getValue(Constants.USER).equals(Constants.LOGIN)) {
                    val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
                    if (!refreshAuthToken(iduser)) {
                        Toasty.error(this@SplashScreen, "Gagal refresh token", Toasty.LENGTH_LONG).show()
                        finish()
                    }

                    when (suratTipe) {
                        "A" -> {
                            startActivity(Intent(this@SplashScreen, SuratMasukActivity::class.java).apply {
                                putExtra("nomerAgenda", noAgenda)
                            })
                            finish()
                        }

                        "B" -> {
                            startActivity(Intent(this@SplashScreen, SuratKeluarActivity::class.java).apply {
                                putExtra("nomerAgenda", noAgenda)
                            })
                            finish()
                        }

                        else -> {
                            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                    finish()
                }
            }, 5000)
        }
    }

    private fun refreshAuthToken(iduserAktivasi: String): Boolean {
        var result = true
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.refreshAuthToken(iduserAktivasi).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@SplashScreen, "refreshAuthToken | onResponse", response.message()
                    )
                    result = false
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@SplashScreen, "refreshAuthToken | onFailure", t.message.toString()
                )
                result = false
            }
        })

        return result
    }
}