package com.jaylangkung.korem.pengaduan

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.PengaduanResponse
import com.jaylangkung.korem.databinding.ActivityPengaduanBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengaduanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengaduanBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: PengaduanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaduanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@PengaduanActivity)
        adapter = PengaduanAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@PengaduanActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getPengaduan(iduser, tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun getPengaduan(iduser_aktivasi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getPengaduan(iduser_aktivasi, tokenAuth).enqueue(object : Callback<PengaduanResponse> {
            override fun onResponse(call: Call<PengaduanResponse>, response: Response<PengaduanResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        adapter.setItem(listData)
                        adapter.notifyItemRangeChanged(0, listData.size)

                        with(binding.rvPengaduanList) {
                            layoutManager = LinearLayoutManager(this@PengaduanActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = this@PengaduanActivity.adapter
                        }
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@PengaduanActivity,
                        "getPengaduan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<PengaduanResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@PengaduanActivity,
                    "getPengaduan | onFailure", t.message.toString()
                )
            }
        })
    }
}