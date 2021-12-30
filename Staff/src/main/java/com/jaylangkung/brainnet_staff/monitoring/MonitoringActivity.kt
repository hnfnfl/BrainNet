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
import com.jaylangkung.brainnet_staff.databinding.ActivityMonitoringBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.EthernetResponse
import com.jaylangkung.brainnet_staff.retrofit.response.UserDCResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonitoringActivity : AppCompatActivity() {

    private lateinit var monitoringBinding: ActivityMonitoringBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var ethernetAdapter: EthernetAdapter
    private lateinit var userDCAdapter: UserDCAdapter
    private var listEthernet: ArrayList<EthernetEntity> = arrayListOf()
    private var listUserDC: ArrayList<UserDCEntity> = arrayListOf()
    private var filterUserDC: ArrayList<UserDCEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitoringBinding = ActivityMonitoringBinding.inflate(layoutInflater)
        setContentView(monitoringBinding.root)
        myPreferences = MySharedPreferences(this@MonitoringActivity)
        ethernetAdapter = EthernetAdapter()
        userDCAdapter = UserDCAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        monitoringBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        getEthernet(tokenAuth)
        getUserDisconnected(tokenAuth)

        monitoringBinding.fabDisconnected.setOnClickListener {
            monitoringBinding.loadingAnim.visibility = View.VISIBLE
            filterUserDC.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                listUserDC.forEach { ListData ->
                    if (ListData.paket != "isolir") {
                        filterUserDC.add(ListData)
                    }
                }
                userDCAdapter.setUserDCItem(filterUserDC)
                userDCAdapter.notifyDataSetChanged()
                monitoringBinding.loadingAnim.visibility = View.GONE
            }, 500)
        }

        monitoringBinding.fabIsolation.setOnClickListener {
            monitoringBinding.loadingAnim.visibility = View.VISIBLE
            filterUserDC.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                listUserDC.forEach { ListData ->
                    if (ListData.paket == "isolir") {
                        filterUserDC.add(ListData)
                    }
                }
                userDCAdapter.setUserDCItem(filterUserDC)
                userDCAdapter.notifyDataSetChanged()
                monitoringBinding.loadingAnim.visibility = View.GONE
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
                        ethernetAdapter.notifyDataSetChanged()

                        with(monitoringBinding.rvEthernet) {
                            layoutManager = LinearLayoutManager(this@MonitoringActivity, LinearLayoutManager.HORIZONTAL, false)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = ethernetAdapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call<EthernetResponse>, t: Throwable) {
                monitoringBinding.loadingAnim.visibility = View.GONE
                Toasty.error(this@MonitoringActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserDisconnected(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getUserDisconnected(tokenAuth).enqueue(object : Callback<UserDCResponse> {
            override fun onResponse(call: Call<UserDCResponse>, response: Response<UserDCResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        monitoringBinding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        listUserDC = listData
                        listUserDC.forEach { ListData ->
                            if (ListData.paket != "isolir") {
                                filterUserDC.add(ListData)
                            }
                        }
                        userDCAdapter.setUserDCItem(filterUserDC)
                        userDCAdapter.notifyDataSetChanged()

                        with(monitoringBinding.rvUserDc) {
                            layoutManager = LinearLayoutManager(this@MonitoringActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = userDCAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        monitoringBinding.empty.visibility = View.VISIBLE
                        monitoringBinding.loadingAnim.visibility = View.GONE
                        listUserDC.clear()
                        userDCAdapter.setUserDCItem(listUserDC)
                        userDCAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<UserDCResponse>, t: Throwable) {
                monitoringBinding.loadingAnim.visibility = View.GONE
                Toasty.error(this@MonitoringActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
            }
        })
    }
}