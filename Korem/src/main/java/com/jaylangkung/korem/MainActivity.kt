package com.jaylangkung.korem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.jaylangkung.korem.cuti.CutiActivity
import com.jaylangkung.korem.dataClass.PostResponse
import com.jaylangkung.korem.databinding.ActivityMainBinding
import com.jaylangkung.korem.giat.TambahGiatActivity
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.survey.SurveyActivity
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@MainActivity)

//        val a = arrayListOf<MutableMap<String, String>>()
//        val tes = mutableMapOf<String, String>()
//        tes["lat"] = "123123"
//        tes["long"] = "asdfasdfas"
//        a.add(tes)
//        val gson = Gson()
//        Log.e("tewts", gson.toJson(a).toString())

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.LOCATION_HARDWARE
                ), 100
            )
        }

        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val jabatan = myPreferences.getValue(Constants.USER_PANGKATJABATAN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val foto = myPreferences.getValue(Constants.FOTO_PATH).toString()
        val jabatanNama = "$jabatan $nama"

        getPost(tokenAuth)
        binding.apply {
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

            btnPresensi.setOnClickListener {
                startActivity(Intent(this@MainActivity, PresensiActivity::class.java))
                finish()
            }

            btnCuti.setOnClickListener {
                startActivity(Intent(this@MainActivity, CutiActivity::class.java))
                finish()
            }

            btnScanAset.setOnClickListener {
                startActivity(Intent(this@MainActivity, ScanAsetActivity::class.java))
                finish()
            }

            btnSurvei.setOnClickListener {
                startActivity(Intent(this@MainActivity, SurveyActivity::class.java))
                finish()
            }

            btnLaporanGiat.setOnClickListener {
                startActivity(Intent(this@MainActivity, TambahGiatActivity::class.java))
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
                &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this@MainActivity, "All Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "All Permission Denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getPost(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getPost(tokenAuth).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                if (response.isSuccessful) {

                    val datas = response.body()!!.data
                    val imageList = ArrayList<SlideModel>() // Create image list
                    for (data in datas) {
                        imageList.add(SlideModel(data.img, data.judul))
                    }

                    val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
                    imageSlider.setImageList(imageList, ScaleTypes.CENTER_INSIDE)
                } else {
                    ErrorHandler().responseHandler(
                        this@MainActivity,
                        "getPost | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MainActivity,
                    "getPost | onFailure", t.message.toString()
                )
            }
        })
    }
}