package com.jaylangkung.brainnet_staff

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.jaylangkung.brainnet_staff.auth.LoginWebAppActivity
import com.jaylangkung.brainnet_staff.data_class.DefaultResponse
import com.jaylangkung.brainnet_staff.data_class.GangguanEntity
import com.jaylangkung.brainnet_staff.data_class.GangguanResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityMainBinding
import com.jaylangkung.brainnet_staff.databinding.BottomSheetMenuPelangganBinding
import com.jaylangkung.brainnet_staff.databinding.BottomSheetMenuPelayananBinding
import com.jaylangkung.brainnet_staff.gangguan.GangguanAdapter
import com.jaylangkung.brainnet_staff.hal_baik.HalBaikActivity
import com.jaylangkung.brainnet_staff.menu_pelanggan.AddCustomerActivity
import com.jaylangkung.brainnet_staff.menu_pelanggan.CustomerActivationActivity
import com.jaylangkung.brainnet_staff.menu_pelanggan.restart.RestartActivity
import com.jaylangkung.brainnet_staff.menu_pelayanan.dispensasi.DispensasiActivity
import com.jaylangkung.brainnet_staff.menu_pelayanan.pemasangan_selesai.PemasanganSelesaiActivity
import com.jaylangkung.brainnet_staff.menu_pelayanan.tambah_gangguan.TambahGangguanActivity
import com.jaylangkung.brainnet_staff.monitoring.MonitoringActivity
import com.jaylangkung.brainnet_staff.notifikasi.NotifikasiActivity
import com.jaylangkung.brainnet_staff.presensi.ScannerActivity
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.settings.SettingActivity
import com.jaylangkung.brainnet_staff.tiang.ScannerTiangActivity
import com.jaylangkung.brainnet_staff.todo_list.TodoActivity
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetMenuPelangganBinding: BottomSheetMenuPelangganBinding
    private lateinit var bottomSheetMenuPelayananBinding: BottomSheetMenuPelayananBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: GangguanAdapter
    private var listGangguanAdapter: ArrayList<GangguanEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@MainActivity)
        adapter = GangguanAdapter()
        askPermission()

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
            addToken(idadmin, deviceToken.toString())
        })

        Firebase.messaging.subscribeToTopic("notifikasi")
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                addToken(idadmin, token)
            } else {
                // Handle the error
                val exception = task.exception
                exception?.message?.let {
                    Log.e(ContentValues.TAG, "Error retrieving FCM registration token: $it")
                }
            }
        }

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

    private fun addToken(idadmin: String, device_token: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.addToken(idadmin, device_token).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Log.d("sukses ", "sukses menambahkan device token")
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@MainActivity, "insertToken | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@MainActivity, "insertToken | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getGangguan(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGangguan(tokenAuth).enqueue(object : Callback<GangguanResponse> {
            override fun onResponse(call: Call<GangguanResponse>, response: Response<GangguanResponse>) {
                if (response.isSuccessful) {
                    binding.llBody.isRefreshing = false
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        binding.empty.visibility = View.GONE
                        val listData = response.body()!!.data
                        listGangguanAdapter = listData
                        adapter.setItem(listGangguanAdapter)
                        adapter.notifyItemRangeChanged(0, listGangguanAdapter.size)

                        with(binding.rvGangguan) {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = this@MainActivity.adapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        listGangguanAdapter.clear()
                        adapter.setItem(listGangguanAdapter)
                        adapter.notifyItemRangeChanged(0, listGangguanAdapter.size)
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@MainActivity, "getGangguan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<GangguanResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@MainActivity, "getGangguan | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun askPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionsToRequest = mutableListOf<String>()

        // Check for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Check for camera, storage, and location permissions
        if (ContextCompat.checkSelfPermission(this@MainActivity, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(cameraPermission)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, readStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(readStoragePermission)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, writeStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(writeStoragePermission)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, locationPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(locationPermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity, permissionsToRequest.toTypedArray(), 100
            )
        }
    }
}