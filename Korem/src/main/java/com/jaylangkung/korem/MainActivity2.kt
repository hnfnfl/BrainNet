package com.jaylangkung.korem

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jaylangkung.korem.dataClass.DataSpinnerResponse
import com.jaylangkung.korem.dataClass.UserSuratSpinnerData
import com.jaylangkung.korem.databinding.ActivityMain2Binding
import com.jaylangkung.korem.retrofit.AuthService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.surat.keluar.SuratKeluarActivity
import com.jaylangkung.korem.surat.masuk.SuratMasukActivity
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var myPreferences: MySharedPreferences

    companion object {
        var listUserSurat = ArrayList<UserSuratSpinnerData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@MainActivity2)

        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val jabatan = myPreferences.getValue(Constants.USER_PANGKATJABATAN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val foto = myPreferences.getValue(Constants.FOTO_PATH).toString()
        val jabatanNama = "$jabatan $nama"

        binding.apply {
            getSpinnerData()
            Glide.with(this@MainActivity2)
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

            btnSuratMasuk.setOnClickListener {
                startActivity(Intent(this@MainActivity2, SuratMasukActivity::class.java))
                finish()
            }

            btnSuratKeluar.setOnClickListener {
                startActivity(Intent(this@MainActivity2, SuratKeluarActivity::class.java))
                finish()
            }
        }
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSuratSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listUserSurat.clear()
                    listUserSurat = response.body()!!.user_surat
                } else {
                    ErrorHandler().responseHandler(
                        this@MainActivity2,
                        "getSuratSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MainActivity2,
                    "getSuratSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }
}