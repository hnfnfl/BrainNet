package com.jaylangkung.indirisma.notifikasi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityNotifikasiBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.NotifikasiResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifikasiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var notifikasiAdapter: NotifikasiAdapter
    private var listNotif: ArrayList<NotifikasiEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@NotifikasiActivity)
        notifikasiAdapter = NotifikasiAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getNotification(tokenAuth)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@NotifikasiActivity, MainActivity::class.java))
        finish()
    }

    private fun getNotification(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getNotification(tokenAuth).enqueue(object : Callback<NotifikasiResponse> {
            override fun onResponse(call: Call<NotifikasiResponse>, response: Response<NotifikasiResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        listNotif = listData
                        notifikasiAdapter.setNotifItem(listNotif)
                        notifikasiAdapter.notifyItemRangeChanged(0, listNotif.size)


                        with(binding.rvNotifikasi) {
                            layoutManager = LinearLayoutManager(this@NotifikasiActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = notifikasiAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        listNotif.clear()
                        notifikasiAdapter.setNotifItem(listNotif)
                        notifikasiAdapter.notifyItemRangeChanged(0, listNotif.size)
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@NotifikasiActivity,
                        "getNotification | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<NotifikasiResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@NotifikasiActivity,
                    "getNotification | onFailure", t.message.toString()
                )
            }
        })
    }
}