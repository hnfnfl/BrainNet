package com.jaylangkung.korem.siapOpsGerak

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.SiapOpsGerakResponse
import com.jaylangkung.korem.databinding.ActivitySiapOpsGerakBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SiapOpsGerakActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySiapOpsGerakBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: SiapOpsGerakAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiapOpsGerakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SiapOpsGerakActivity)
        adapter = SiapOpsGerakAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SiapOpsGerakActivity, MainActivity::class.java))
                finish()
            }
        })

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSiapOpsGerak(tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }
    }

    private fun getSiapOpsGerak(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getSiapOpsGerak(tokenAuth).enqueue(object : Callback<SiapOpsGerakResponse> {
            override fun onResponse(call: Call<SiapOpsGerakResponse>, response: Response<SiapOpsGerakResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        adapter.setItem(listData)
                        adapter.notifyItemRangeChanged(0, listData.size)

                        with(binding.rvSiapOps) {
                            layoutManager = LinearLayoutManager(this@SiapOpsGerakActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = this@SiapOpsGerakActivity.adapter
                        }
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@SiapOpsGerakActivity,
                        "getSiapOpsGerak | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<SiapOpsGerakResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@SiapOpsGerakActivity,
                    "getSiapOpsGerak | onFailure", t.message.toString()
                )
            }
        })
    }
}