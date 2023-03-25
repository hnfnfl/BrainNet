package com.jaylangkung.korem.giat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.GiatResponse
import com.jaylangkung.korem.databinding.ActivityGiatBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GiatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiatBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: GiatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@GiatActivity)
        adapter = GiatAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@GiatActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getGiat(iduser, tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            fabAddGiat.setOnClickListener {
                startActivity(Intent(this@GiatActivity, TambahGiatActivity::class.java))
                finish()
            }
        }
    }

    private fun getGiat(iduser_aktivasi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGiat(iduser_aktivasi, tokenAuth).enqueue(object : Callback<GiatResponse> {
            override fun onResponse(call: Call<GiatResponse>, response: Response<GiatResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        adapter.setItem(listData)
                        adapter.notifyItemRangeChanged(0, listData.size)

                        with(binding.rvGiatList) {
                            layoutManager = LinearLayoutManager(this@GiatActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = this@GiatActivity.adapter
                        }
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@GiatActivity,
                        "getGiat | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<GiatResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@GiatActivity,
                    "getGiat | onFailure", t.message.toString()
                )
            }
        })
    }
}