package com.jaylangkung.korem.cuti

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.CutiResponse
import com.jaylangkung.korem.databinding.ActivityCutiBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CutiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCutiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var cutiAdapter: CutiAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCutiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@CutiActivity)
        cutiAdapter = CutiAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@CutiActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getCuti(iduser, tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            fabAddCuti.setOnClickListener {
                startActivity(Intent(this@CutiActivity, TambahCutiActivity::class.java))
                finish()
            }
        }
    }

    private fun getCuti(iduser_aktivasi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getCuti(iduser_aktivasi, tokenAuth).enqueue(object : Callback<CutiResponse> {
            override fun onResponse(call: Call<CutiResponse>, response: Response<CutiResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        cutiAdapter.setItem(listData)
                        cutiAdapter.notifyItemRangeChanged(0, listData.size)

                        with(binding.rvCutiList) {
                            layoutManager = LinearLayoutManager(this@CutiActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = cutiAdapter
                        }
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@CutiActivity,
                        "getCuti | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<CutiResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@CutiActivity,
                    "getCuti | onFailure", t.message.toString()
                )
            }
        })
    }
}