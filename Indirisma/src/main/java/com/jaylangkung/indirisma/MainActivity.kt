package com.jaylangkung.indirisma

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.jaylangkung.indirisma.auth.LoginWebAppActivity
import com.jaylangkung.indirisma.databinding.ActivityMainBinding
import com.jaylangkung.indirisma.databinding.BottomSheetMenuPelangganBinding
import com.jaylangkung.indirisma.databinding.BottomSheetMenuPelayananBinding
import com.jaylangkung.indirisma.gangguan.GangguanAdapter
import com.jaylangkung.indirisma.gangguan.GangguanEntity
import com.jaylangkung.indirisma.hal_baik.HalBaikActivity
import com.jaylangkung.indirisma.menu_pelanggan.AddCustomerActivity
import com.jaylangkung.indirisma.menu_pelanggan.CustomerActivationActivity
import com.jaylangkung.indirisma.menu_pelanggan.restart.RestartActivity
import com.jaylangkung.indirisma.menu_pelayanan.dispensasi.DispensasiActivity
import com.jaylangkung.indirisma.menu_pelayanan.pemasangan_selesai.PemasanganSelesaiActivity
import com.jaylangkung.indirisma.menu_pelayanan.tambah_gangguan.TambahGangguanActivity
import com.jaylangkung.indirisma.monitoring.MonitoringActivity
import com.jaylangkung.indirisma.notifikasi.NotifikasiActivity
import com.jaylangkung.indirisma.presensi.ScannerActivity
import com.jaylangkung.indirisma.retrofit.AuthService
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.retrofit.response.GangguanResponse
import com.jaylangkung.indirisma.settings.SettingActivity
import com.jaylangkung.indirisma.tiang.ScannerTiangActivity
import com.jaylangkung.indirisma.todo_list.TodoActivity
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetMenuPelangganBinding: BottomSheetMenuPelangganBinding
    private lateinit var bottomSheetMenuPelayananBinding: BottomSheetMenuPelayananBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var gangguanAdapter: GangguanAdapter
    private var listGangguanAdapter: ArrayList<GangguanEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val deviceToken = task.result
            insertToken(idadmin, deviceToken.toString())
        })

        Firebase.messaging.subscribeToTopic("notifikasi_indir")

        getGangguan(tokenAuth)

        binding.apply {
            Glide.with(this@MainActivity)
                .load(foto)
                .apply(RequestOptions().override(120))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(imgPhoto)

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            tvGreetings.text = when (currentHour) {
                in 4..11 -> getString(R.string.greetings, "Selamat Pagi", nama)
                in 12..14 -> getString(R.string.greetings, "Selamat Siang", nama)
                in 15..17 -> getString(R.string.greetings, "Selamat Sore", nama)
                else -> getString(R.string.greetings, "Selamat Malam", nama)
            }

            btnSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                finish()
            }

            btnNotification.setOnClickListener {
                startActivity(Intent(this@MainActivity, NotifikasiActivity::class.java))
                finish()
            }

            llMonitoring.setOnClickListener {
                startActivity(Intent(this@MainActivity, MonitoringActivity::class.java))
                finish()
            }

            fabPresensi.setOnClickListener {
                startActivity(Intent(this@MainActivity, ScannerActivity::class.java))
                finish()
            }

            fabTiang.setOnClickListener {
                startActivity(Intent(this@MainActivity, ScannerTiangActivity::class.java))
                finish()
            }

            fabWebApp.setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginWebAppActivity::class.java))
                finish()
            }

            llMenuUser.setOnClickListener {
                bottomSheetMenuPelangganBinding = BottomSheetMenuPelangganBinding.inflate(layoutInflater)

                val dialog = BottomSheetDialog(this@MainActivity)

                bottomSheetMenuPelangganBinding.llRestart.setOnClickListener {
                    startActivity(Intent(this@MainActivity, RestartActivity::class.java))
                    finish()
                    dialog.dismiss()
                }

                bottomSheetMenuPelangganBinding.llAddCustomer.setOnClickListener {
                    startActivity(Intent(this@MainActivity, AddCustomerActivity::class.java))
                    finish()
                    dialog.dismiss()
                }

                bottomSheetMenuPelangganBinding.llActivateCustomer.setOnClickListener {
                    startActivity(Intent(this@MainActivity, CustomerActivationActivity::class.java))
                    finish()
                    dialog.dismiss()
                }

                dialog.setCancelable(true)
                dialog.setContentView(bottomSheetMenuPelangganBinding.root)
                dialog.show()
            }

            llServices.setOnClickListener {
                bottomSheetMenuPelayananBinding = BottomSheetMenuPelayananBinding.inflate(layoutInflater)

                val dialog = BottomSheetDialog(this@MainActivity)

                bottomSheetMenuPelayananBinding.llInsertInterference.setOnClickListener {
                    startActivity(Intent(this@MainActivity, TambahGangguanActivity::class.java))
                    finish()
                    dialog.dismiss()
                }

                bottomSheetMenuPelayananBinding.llInstallationComplete.setOnClickListener {
                    startActivity(Intent(this@MainActivity, PemasanganSelesaiActivity::class.java))
                    finish()
                    dialog.dismiss()
                }

                bottomSheetMenuPelayananBinding.llPayment.setOnClickListener {
                    Toasty.info(this@MainActivity, "Fitur ini sedang dalam pengembangan").show()
//                    startActivity(Intent(this@MainActivity, PembayaranActivity::class.java))
//                    finish()
                    dialog.dismiss()
                }

                bottomSheetMenuPelayananBinding.llDispensation.setOnClickListener {
                    startActivity(Intent(this@MainActivity, DispensasiActivity::class.java))
                    finish()
                    dialog.dismiss()
                }

                dialog.setCancelable(true)
                dialog.setContentView(bottomSheetMenuPelayananBinding.root)
                dialog.show()
            }

            llGoodThings.setOnClickListener {
                startActivity(Intent(this@MainActivity, HalBaikActivity::class.java))
                finish()
            }

            llTodoList.setOnClickListener {
                startActivity(Intent(this@MainActivity, TodoActivity::class.java))
                finish()
            }

            llBody.setOnRefreshListener {
                binding.loadingAnim.visibility = View.VISIBLE
                getGangguan(tokenAuth)
            }
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
        service.addToken(idadmin, device_token, "true").enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Log.d("sukses ", "sukses menambahkan device token")
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@MainActivity,
                        "insertToken | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MainActivity,
                    "insertToken | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getGangguan(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGangguan(tokenAuth, "true").enqueue(object : Callback<GangguanResponse> {
            override fun onResponse(call: Call<GangguanResponse>, response: Response<GangguanResponse>) {
                if (response.isSuccessful) {
                    binding.llBody.isRefreshing = false
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        binding.empty.visibility = View.GONE
                        val listData = response.body()!!.data
                        listGangguanAdapter = listData
                        gangguanAdapter.setItem(listGangguanAdapter)
                        gangguanAdapter.notifyItemRangeChanged(0, listGangguanAdapter.size)

                        with(binding.rvGangguan) {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = gangguanAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        listGangguanAdapter.clear()
                        gangguanAdapter.setItem(listGangguanAdapter)
                        gangguanAdapter.notifyItemRangeChanged(0, listGangguanAdapter.size)
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@MainActivity,
                        "getGangguan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<GangguanResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@MainActivity,
                    "getGangguan | onFailure", t.message.toString()
                )
            }
        })
    }

}