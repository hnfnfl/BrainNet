package com.jaylangkung.brainnet_staff

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.auth.LoginActivity
import com.jaylangkung.brainnet_staff.databinding.ActivitySplashScreenBinding
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.LoginResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashScreenBinding: ActivitySplashScreenBinding
    private lateinit var myPreferences: MySharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashScreenBinding.root)
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
                    ErrorHandler().responseHandler(this@SplashScreenActivity, response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                ErrorHandler().responseHandler(this@SplashScreenActivity, t.message.toString())
            }
        })
    }

}