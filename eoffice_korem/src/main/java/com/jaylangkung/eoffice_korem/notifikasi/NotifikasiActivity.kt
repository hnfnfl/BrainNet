package com.jaylangkung.eoffice_korem.notifikasi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.eoffice_korem.MainActivity
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.NotifikasiResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityNotifikasiBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifikasiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: NotifikasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@NotifikasiActivity)
        adapter = NotifikasiAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@NotifikasiActivity, MainActivity::class.java))
                finish()
            }
        })

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            getNotifikasi(iduser, tokenAuth)
        }
    }

    private fun getNotifikasi(iduser: String, tokenAuth: String) {
        binding.loadingAnim.visibility = View.VISIBLE
        binding.rvNotifikasi.visibility = View.VISIBLE
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.getNotifikasi(iduser, tokenAuth).enqueue(object : Callback<NotifikasiResponse> {
                override fun onResponse(call: Call<NotifikasiResponse>, response: Response<NotifikasiResponse>) {
                    if (response.isSuccessful) {
                        val listData = response.body()!!.data
                        if (response.body()!!.status == "success") {
                            binding.loadingAnim.visibility = View.GONE
                            binding.empty.visibility = View.GONE
                            adapter.setItem(listData)
                            adapter.notifyItemRangeChanged(0, listData.size)

                            with(binding.rvNotifikasi) {
                                layoutManager = LinearLayoutManager(this@NotifikasiActivity)
                                itemAnimator = DefaultItemAnimator()
                                setHasFixedSize(true)
                                adapter = this@NotifikasiActivity.adapter
                            }
                        } else if (response.body()!!.status == "empty") {
                            binding.apply {
                                empty.visibility = View.VISIBLE
                                loadingAnim.visibility = View.GONE
                            }
                            adapter.clearItem()
                        }
                    } else {
                        binding.loadingAnim.visibility = View.GONE
                        ErrorHandler().responseHandler(
                            this@NotifikasiActivity, "getNotifikasi | onResponse", response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<NotifikasiResponse>, t: Throwable) {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@NotifikasiActivity, "getNotifikasi | onFailure", t.message.toString()
                    )
                }
            })
    }
}