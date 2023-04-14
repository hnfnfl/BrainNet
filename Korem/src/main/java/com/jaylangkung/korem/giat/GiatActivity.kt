package com.jaylangkung.korem.giat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.GiatResponse
import com.jaylangkung.korem.databinding.ActivityGiatBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GiatActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityGiatBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: GiatAdapter

    private lateinit var client: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@GiatActivity)
        adapter = GiatAdapter()

        if (ContextCompat.checkSelfPermission(this@GiatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this@GiatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@GiatActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.LOCATION_HARDWARE
                ), 100
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@GiatActivity, MainActivity::class.java))
                finish()
            }
        })

        client = LocationServices.getFusedLocationProviderClient(this@GiatActivity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.giat_all_map) as SupportMapFragment
        mapFragment.getMapAsync(this@GiatActivity)

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

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //permission access fine & coarse location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toasty.error(this, "Mohon izinkan lokasi pada aplikasi", Toasty.LENGTH_LONG).show()
            onBackPressedDispatcher.onBackPressed()
            return
        }
//        client.lastLocation.addOnCompleteListener {
//            Priority.PRIORITY_HIGH_ACCURACY
//            val userLat = it.result.latitude
//            val userLong = it.result.longitude
//            val pos = LatLng(userLat, userLong)
//            Log.e("posisi user", "$userLat, $userLong")
//            drawMarker(pos)
//        }
    }

    private fun getGiat(iduser_aktivasi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGiat(iduser_aktivasi, tokenAuth).enqueue(object : Callback<GiatResponse> {
            override fun onResponse(call: Call<GiatResponse>, response: Response<GiatResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        val listData = response.body()!!.data
                        for (data in listData) {
                            val lat = data.posisi_giat[0].lat.toDouble()
                            val lng = data.posisi_giat[0].lng.toDouble()
                            val giatPos = LatLng(lat, lng)
                            mMap.setOnMapLoadedCallback {
                                drawMarker(data.tujuan, giatPos)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GiatResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@GiatActivity,
                    "getGiat | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun drawMarker(title: String, pos: LatLng) {
        val markerOptions = MarkerOptions()
            .position(pos)
            .title(title)
            .draggable(true)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        val show = mMap.addMarker(markerOptions)
        show?.showInfoWindow()
    }
}