package com.jaylangkung.demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jaylangkung.demo.auth.LoginWebAppActivity
import com.jaylangkung.demo.databinding.ActivityMainBinding
import com.jaylangkung.demo.presensi.PresensiActivity
import com.jaylangkung.demo.retrofit.AuthService
import com.jaylangkung.demo.retrofit.RetrofitClient
import com.jaylangkung.demo.retrofit.response.LoginResponse
import com.jaylangkung.demo.utils.Constants
import com.jaylangkung.demo.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var myPreferences: MySharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        myPreferences = MySharedPreferences(this@MainActivity)


        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }

        val nama = myPreferences.getValue(Constants.USER_NAMA)
        val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val foto = myPreferences.getValue(Constants.FOTO_PATH).toString()
        val deviceID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        Glide.with(this@MainActivity)
            .load(foto)
            .apply(RequestOptions().override(120))
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(mainBinding.imgPhoto)

        refreshAuthToken(email, idadmin, "hp.$deviceID")

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mainBinding.tvGreetings.text = when (currentHour) {
            in 4..11 -> getString(R.string.greetings, "Selamat Pagi", nama)
            in 12..14 -> getString(R.string.greetings, "Selamat Siang", nama)
            in 15..17 -> getString(R.string.greetings, "Selamat Sore", nama)
            else -> getString(R.string.greetings, "Selamat Malam", nama)
        }

        mainBinding.fabPresensi.setOnClickListener {
            startActivity(Intent(this@MainActivity, PresensiActivity::class.java))
            finish()
        }

        mainBinding.fabWebApp.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginWebAppActivity::class.java))
            finish()
        }

        mainBinding.llBody.setOnRefreshListener {
            refreshAuthToken(email, idadmin, "hp.$deviceID")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }

    private fun refreshAuthToken(email: String, idadmin: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.refreshAuthToken(email, idadmin, deviceID).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toasty.error(this@MainActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }
}