package com.jaylangkung.indirisma

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.auth.LoginActivity
import com.jaylangkung.indirisma.databinding.ActivitySplashScreenBinding
import com.jaylangkung.indirisma.retrofit.AuthService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.LoginResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var myPreferences: MySharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SplashScreenActivity)

        Handler(Looper.getMainLooper()).postDelayed({
            //Ketika user sudah login tidak perlu ke halaman login lagi
            if (myPreferences.getValue(Constants.USER).equals(Constants.LOGIN)) {
                val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
                val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
                val deviceID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
                refreshAuthToken(email, idadmin, "hp.$deviceID")
            } else {
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                finish()
            }
        }, 500)
    }

    private fun refreshAuthToken(email: String, idadmin: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.refreshAuthToken(email, idadmin, deviceID).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@SplashScreenActivity,
                        "refreshAuthToken | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@SplashScreenActivity,
                    "refreshAuthToken | onFailure", t.message.toString()
                )
            }
        })
    }

}