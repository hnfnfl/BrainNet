package com.jaylangkung.brainnet_staff.menu_pelanggan.restart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityRestartBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.UserResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestartActivity : AppCompatActivity() {

    private lateinit var restartBinding: ActivityRestartBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var userAdapter: UserAdapter
    private var userSearch: ArrayList<UserEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restartBinding = ActivityRestartBinding.inflate(layoutInflater)
        setContentView(restartBinding.root)
        myPreferences = MySharedPreferences(this@RestartActivity)
        userAdapter = UserAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getPelanggan(tokenAuth)

        restartBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        restartBinding.svProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                userAdapter.filter.filter(query)
                return true
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@RestartActivity, MainActivity::class.java))
        finish()
    }

    private fun getPelanggan(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getPelanggan(tokenAuth).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        restartBinding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        userSearch = listData
                        userAdapter.setUserItem(userSearch)
                        userAdapter.notifyItemRangeChanged(0, userSearch.size)

                        with(restartBinding.rvUserList) {
                            layoutManager = LinearLayoutManager(this@RestartActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = userAdapter
                        }
                    }
                } else {
                    restartBinding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@RestartActivity,
                        "getPelanggan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                restartBinding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@RestartActivity,
                    "getPelanggan | onFailure", t.message.toString()
                )
            }
        })
    }
}