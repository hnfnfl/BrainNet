package com.jaylangkung.eoffice_korem

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.eoffice_korem.auth.LoginActivity
import com.jaylangkung.eoffice_korem.dataClass.UserResponse
import com.jaylangkung.eoffice_korem.databinding.ActivitySplashScreenBinding
import com.jaylangkung.eoffice_korem.retrofit.AuthService
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
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

        Handler(Looper.getMainLooper()).postDelayed({
            //Ketika user sudah login tidak perlu ke halaman login lagi
            if (myPreferences.getValue(Constants.USER).equals(Constants.LOGIN)) {
                val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
                refreshAuthToken(iduser)
            } else {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                finish()
            }
        }, 500)
    }

    private fun refreshAuthToken(iduser_aktivasi: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.refreshAuthToken(iduser_aktivasi).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@SplashScreen, "refreshAuthToken | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@SplashScreen, "refreshAuthToken | onFailure", t.message.toString()
                )
            }
        })
    }
}