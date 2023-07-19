package com.jaylangkung.brainnet_staff.monitoring

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.EthernetEntity
import com.jaylangkung.brainnet_staff.data_class.EthernetResponse
import com.jaylangkung.brainnet_staff.data_class.UserDCEntity
import com.jaylangkung.brainnet_staff.data_class.UserDCResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityMonitoringBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitoringActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonitoringBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var ethernetAdapter: EthernetAdapter
    private lateinit var userDCAdapter: UserDCAdapter
    private var listEthernet: ArrayList<EthernetEntity> = arrayListOf()
    private var listUserDC: ArrayList<UserDCEntity> = arrayListOf()
    private var filterUserDC: ArrayList<UserDCEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@MonitoringActivity)
        ethernetAdapter = EthernetAdapter()
        userDCAdapter = UserDCAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        getEthernet(tokenAuth)
        getUserDisconnected(tokenAuth)

        binding.fabDisconnected.setOnClickListener {
            binding.loadingAnim.visibility = View.VISIBLE
            filterUserDC.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                listUserDC.forEach { ListData ->
                    if (ListData.paket != "isolir") {
                        filterUserDC.add(ListData)
                    }
                }
                userDCAdapter.setUserDCItem(filterUserDC)
                userDCAdapter.notifyItemRangeChanged(0, filterUserDC.size)
                binding.loadingAnim.visibility = View.GONE
            }, 500)
        }

        binding.fabIsolation.setOnClickListener {
            binding.loadingAnim.visibility = View.VISIBLE
            filterUserDC.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                listUserDC.forEach { ListData ->
                    if (ListData.paket == "isolir") {
                        filterUserDC.add(ListData)
                    }
                }
                userDCAdapter.setUserDCItem(filterUserDC)
                userDCAdapter.notifyItemRangeChanged(0, filterUserDC.size)
                binding.loadingAnim.visibility = View.GONE
            }, 500)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@MonitoringActivity, MainActivity::class.java))
        finish()
    }

    private fun getEthernet(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getEthernet(tokenAuth).enqueue(object : Callback<EthernetResponse> {
            override fun onResponse(call: Call<EthernetResponse>, response: Response<EthernetResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        val listData = response.body()!!.data
                        listEthernet = listData
                        ethernetAdapter.setEthernetItem(listEthernet)
                        ethernetAdapter.notifyItemRangeChanged(0, filterUserDC.size)

                        with(binding.rvEthernet) {
                            layoutManager = LinearLayoutManager(this@MonitoringActivity, LinearLayoutManager.HORIZONTAL, false)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = ethernetAdapter
                        }
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@MonitoringActivity,
                        "getEthernet | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<EthernetResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@MonitoringActivity,
                    "getEthernet | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getUserDisconnected(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getUserDisconnected(tokenAuth).enqueue(object : Callback<UserDCResponse> {
            override fun onResponse(call: Call<UserDCResponse>, response: Response<UserDCResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        listUserDC = listData
                        listUserDC.forEach { ListData ->
                            if (ListData.paket != "isolir") {
                                filterUserDC.add(ListData)
                            }
                        }
                        userDCAdapter.setUserDCItem(filterUserDC)
                        userDCAdapter.notifyItemRangeChanged(0, filterUserDC.size)

                        with(binding.rvUserDc) {
                            layoutManager = LinearLayoutManager(this@MonitoringActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = userDCAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        listUserDC.clear()
                        userDCAdapter.setUserDCItem(listUserDC)
                        userDCAdapter.notifyItemRangeChanged(0, filterUserDC.size)
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@MonitoringActivity,
                        "getUserDisconnected | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<UserDCResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@MonitoringActivity,
                    "getUserDisconnected | onFailure", t.message.toString()
                )
            }
        })
    }

}