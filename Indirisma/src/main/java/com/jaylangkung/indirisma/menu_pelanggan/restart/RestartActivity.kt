package com.jaylangkung.indirisma.menu_pelanggan.restart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityRestartBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.UserResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestartBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var userAdapter: UserAdapter
    private var userSearch: ArrayList<UserEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@RestartActivity)
        userAdapter = UserAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getPelanggan(tokenAuth)

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }

            svProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    userAdapter.filter.filter(query)
                    return true
                }
            })
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@RestartActivity, MainActivity::class.java))
        finish()
    }

    private fun getPelanggan(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getPelanggan(tokenAuth, "true").enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        userSearch = listData
                        userAdapter.setItem(userSearch)
                        userAdapter.notifyItemRangeChanged(0, userSearch.size)

                        with(binding.rvUserList) {
                            layoutManager = LinearLayoutManager(this@RestartActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = userAdapter
                        }
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@RestartActivity,
                        "getPelanggan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@RestartActivity,
                    "getPelanggan | onFailure", t.message.toString()
                )
            }
        })
    }
}