package com.jaylangkung.eoffice_korem

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.jaylangkung.eoffice_korem.auth.LoginWebappActivity
import com.jaylangkung.eoffice_korem.dataClass.DataSpinnerResponse
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.UserSuratSpinnerData
import com.jaylangkung.eoffice_korem.databinding.ActivityMainBinding
import com.jaylangkung.eoffice_korem.notifikasi.NotifikasiActivity
import com.jaylangkung.eoffice_korem.profile.ProfileActivity
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
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myPreferences: MySharedPreferences

    companion object {
        var listUserSurat = ArrayList<UserSuratSpinnerData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@MainActivity)
        askPermission()

        Firebase.messaging.subscribeToTopic("eoffice_korem")
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
                addToken(iduser, token)
            } else {
                // Handle the error
                val exception = task.exception
                exception?.message?.let {
                    Log.e(TAG, "Error retrieving FCM registration token: $it")
                }
            }
        }

        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val jabatan = myPreferences.getValue(Constants.USER_PANGKATJABATAN).toString()
        val foto = myPreferences.getValue(Constants.FOTO_PATH).toString()
        val jabatanNama = "$jabatan $nama"

        binding.apply {
            getSpinnerData()
            Glide.with(this@MainActivity)
                .load(foto)
                .apply(RequestOptions().override(120))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(imgPhoto)

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            tvGreetings.text = when (currentHour) {
                in 4..11 -> getString(R.string.greetings, "Selamat Pagi", jabatanNama)
                in 12..14 -> getString(R.string.greetings, "Selamat Siang", jabatanNama)
                in 15..17 -> getString(R.string.greetings, "Selamat Sore", jabatanNama)
                else -> getString(R.string.greetings, "Selamat Malam", jabatanNama)
            }

            imgPhoto.setOnClickListener {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                finish()
            }

            btnNotification.setOnClickListener {
                startActivity(Intent(this@MainActivity, NotifikasiActivity::class.java))
                finish()
            }

            btnSuratMasuk.setOnClickListener {
                startActivity(Intent(this@MainActivity, SuratMasukActivity::class.java))
                finish()
            }

            btnSuratKeluar.setOnClickListener {
                startActivity(Intent(this@MainActivity, SuratKeluarActivity::class.java))
                finish()
            }

            fabLoginWebapp.setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginWebappActivity::class.java))
                finish()
            }
        }
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSuratSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(
                call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>
            ) {
                if (response.isSuccessful) {
                    listUserSurat.clear()
                    listUserSurat = response.body()!!.user_surat
                } else {
                    ErrorHandler().responseHandler(
                        this@MainActivity, "getSuratSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MainActivity, "getSuratSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun addToken(iduser_aktivasi: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.addToken(iduser_aktivasi, deviceID).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Log.d("addToken", response.body()!!.message)
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@MainActivity, "addToken | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MainActivity, "addToken | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun askPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionsToRequest = mutableListOf<String>()

        // Check for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Check for camera, storage, and location permissions
        if (ContextCompat.checkSelfPermission(this@MainActivity, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(cameraPermission)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, readStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(readStoragePermission)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, writeStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(writeStoragePermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity, permissionsToRequest.toTypedArray(), 100
            )
        }
    }
}