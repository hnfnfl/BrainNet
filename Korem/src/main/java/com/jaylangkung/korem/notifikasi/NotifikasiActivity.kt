package com.jaylangkung.korem.notifikasi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.NotifikasiData
import com.jaylangkung.korem.dataClass.NotifikasiResponse
import com.jaylangkung.korem.databinding.ActivityNotifikasiBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifikasiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var notifikasiAdapter: NotifikasiAdapter
    private var listNotif: ArrayList<NotifikasiData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@NotifikasiActivity)
        notifikasiAdapter = NotifikasiAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@NotifikasiActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getNotification(iduser, tokenAuth)

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun getNotification(iduser_aktivasi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getNotification(iduser_aktivasi, tokenAuth).enqueue(object : Callback<NotifikasiResponse> {
            override fun onResponse(call: Call<NotifikasiResponse>, response: Response<NotifikasiResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        listNotif = response.body()!!.data
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