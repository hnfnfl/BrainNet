package com.jaylangkung.brainnet_staff

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.jaylangkung.brainnet_staff.auth.LoginWebAppActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityMainBinding
import com.jaylangkung.brainnet_staff.gangguan.GangguanAdapter
import com.jaylangkung.brainnet_staff.gangguan.GangguanEntity
import com.jaylangkung.brainnet_staff.hal_baik.HalBaikActivity
import com.jaylangkung.brainnet_staff.monitoring.MonitoringActivity
import com.jaylangkung.brainnet_staff.notifikasi.NotifikasiActivity
import com.jaylangkung.brainnet_staff.presensi.ScannerActivity
import com.jaylangkung.brainnet_staff.restart.RestartActivity
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.GangguanResponse
import com.jaylangkung.brainnet_staff.settings.SettingActivity
import com.jaylangkung.brainnet_staff.tiang.ScannerTiangActivity
import com.jaylangkung.brainnet_staff.todo_list.TodoActivity
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

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
                100
            )
        }

        val nama = myPreferences.getValue(Constants.USER_NAMA)
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val foto = myPreferences.getValue(Constants.FOTO_PATH).toString()

        Glide.with(this@MainActivity)
            .load(foto)
            .apply(RequestOptions().override(120))
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(mainBinding.imgPhoto)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val deviceToken = task.result
            insertToken(idadmin, deviceToken.toString())
        })

        Firebase.messaging.subscribeToTopic("notifikasi")

        getGangguan(tokenAuth)

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mainBinding.tvGreetings.text = when (currentHour) {
            in 4..11 -> getString(R.string.greetings, "Selamat Pagi", nama)
            in 12..14 -> getString(R.string.greetings, "Selamat Siang", nama)
            in 15..17 -> getString(R.string.greetings, "Selamat Sore", nama)
            else -> getString(R.string.greetings, "Selamat Malam", nama)
        }

        mainBinding.btnNotification.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotifikasiActivity::class.java))
            finish()
        }

        mainBinding.llMonitoring.setOnClickListener {
            startActivity(Intent(this@MainActivity, MonitoringActivity::class.java))
            finish()
        }

        mainBinding.fabPresensi.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScannerActivity::class.java))
            finish()
        }

        mainBinding.fabTiang.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScannerTiangActivity::class.java))
            finish()
        }

        mainBinding.fabWebApp.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginWebAppActivity::class.java))
            finish()
        }

        mainBinding.llRestart.setOnClickListener {
            startActivity(Intent(this@MainActivity, RestartActivity::class.java))
            finish()
        }

        mainBinding.llGoodThings.setOnClickListener {
            startActivity(Intent(this@MainActivity, HalBaikActivity::class.java))
            finish()
        }

        mainBinding.llTodoList.setOnClickListener {
            startActivity(Intent(this@MainActivity, TodoActivity::class.java))
            finish()
        }

        mainBinding.llSettings.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            finish()
        }

        mainBinding.llBody.setOnRefreshListener {
            mainBinding.loadingAnim.visibility = View.VISIBLE
            getGangguan(tokenAuth)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }

    private fun insertToken(idadmin: String, device_token: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.addToken(idadmin, device_token).enqueue(object : Callback<DefaultResponse> {
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

    private fun getGangguan(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGangguan(tokenAuth).enqueue(object : Callback<GangguanResponse> {
            override fun onResponse(call: Call<GangguanResponse>, response: Response<GangguanResponse>) {
                if (response.isSuccessful) {
                    mainBinding.llBody.isRefreshing = false
                    if (response.body()!!.status == "success") {
                        mainBinding.loadingAnim.visibility = View.GONE
                        mainBinding.empty.visibility = View.GONE
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
                        listGangguanAdapter.clear()
                        gangguanAdapter.setListGangguanItem(listGangguanAdapter)
                        gangguanAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toasty.error(this@MainActivity, response.message(), Toasty.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GangguanResponse>, t: Throwable) {
                Toasty.error(this@MainActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
            }
        })
    }
}