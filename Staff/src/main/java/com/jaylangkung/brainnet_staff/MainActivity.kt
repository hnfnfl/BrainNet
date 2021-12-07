package com.jaylangkung.brainnet_staff

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.jaylangkung.brainnet_staff.databinding.ActivityMainBinding
import com.jaylangkung.brainnet_staff.gangguan.GangguanAdapter
import com.jaylangkung.brainnet_staff.gangguan.GangguanEntity
import com.jaylangkung.brainnet_staff.hal_baik.HalBaikActivity
import com.jaylangkung.brainnet_staff.restart.RestartActivity
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.GangguanResponse
import com.jaylangkung.brainnet_staff.retrofit.response.LoginResponse
import com.jaylangkung.brainnet_staff.scanner.ScannerActivity
import com.jaylangkung.brainnet_staff.settings.SettingActivity
import com.jaylangkung.brainnet_staff.tiang.ScannerTiangActivity
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var gangguanAdapter: GangguanAdapter
    private var listGangguanAdapter: ArrayList<GangguanEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        myPreferences = MySharedPreferences(this@MainActivity)
        gangguanAdapter = GangguanAdapter()

        val nama = myPreferences.getValue(Constants.USER_NAMA)
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val deviceToken = task.result
            insertToken(idadmin, deviceToken.toString())
        })

        refreshAuthToken(idadmin)
        getGangguan(tokenAuth)

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mainBinding.tvGreetings.text = when (currentHour) {
            in 4..11 -> getString(R.string.greetings, "Selamat Pagi", nama)
            in 12..14 -> getString(R.string.greetings, "Selamat Siang", nama)
            in 15..17 -> getString(R.string.greetings, "Selamat Sore", nama)
            else -> getString(R.string.greetings, "Selamat Malam", nama)
        }

        mainBinding.llScanner.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScannerActivity::class.java))
            finish()
        }

        mainBinding.llRestart.setOnClickListener {
            startActivity(Intent(this@MainActivity, RestartActivity::class.java))
            finish()
        }

        mainBinding.llTiang.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScannerTiangActivity::class.java))
            finish()
        }

        mainBinding.llGoodThings.setOnClickListener {
            startActivity(Intent(this@MainActivity, HalBaikActivity::class.java))
            finish()
        }

        mainBinding.llSettings.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            finish()
        }

        mainBinding.llBody.setOnRefreshListener {
            mainBinding.loadingAnim.visibility = View.VISIBLE
            getGangguan(tokenAuth)
            refreshAuthToken(idadmin)
        }
    }

    private fun insertToken(idpenjual: String, device_token: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.addToken(idpenjual, device_token).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Log.d("sukses ", "sukses menambahkan device token")
                    }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toasty.error(this@MainActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }

    private fun refreshAuthToken(idpenjual: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.refreshAuthToken(idpenjual).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toasty.error(this@MainActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }

    private fun getGangguan(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGangguan(tokenAuth).enqueue(object : Callback<GangguanResponse> {
            override fun onResponse(call: Call<GangguanResponse>, response: Response<GangguanResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        mainBinding.loadingAnim.visibility = View.GONE
                        mainBinding.llBody.isRefreshing = false
                        val listData = response.body()!!.data
                        listGangguanAdapter = listData
                        gangguanAdapter.setListGangguanItem(listGangguanAdapter)
                        gangguanAdapter.notifyDataSetChanged()

                        with(mainBinding.rvGangguan) {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = gangguanAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        mainBinding.empty.visibility = View.VISIBLE
                        mainBinding.loadingAnim.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<GangguanResponse>, t: Throwable) {
                Toasty.error(this@MainActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
            }
        })
    }
}