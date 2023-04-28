package com.jaylangkung.korem.giat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.GiatData
import com.jaylangkung.korem.dataClass.GiatResponse
import com.jaylangkung.korem.databinding.ActivityGiatBinding
import com.jaylangkung.korem.databinding.BottomSheetGiatDetailBinding
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

    private lateinit var client: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    private var listGiat: ArrayList<GiatData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@GiatActivity)
        client = LocationServices.getFusedLocationProviderClient(this@GiatActivity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.giat_all_map) as SupportMapFragment
        mapFragment.getMapAsync(this@GiatActivity)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.LOCATION_HARDWARE
        )

        if (permissions.all { ContextCompat.checkSelfPermission(this@GiatActivity, it) == PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this@GiatActivity, permissions, 100)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@GiatActivity, MainActivity::class.java))
                finish()
            }
        })

        getGiat(getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString()))

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //check permission access fine & coarse location
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationPermission || !coarseLocationPermission) {
            Toasty.error(this, "Mohon izinkan lokasi pada aplikasi", Toasty.LENGTH_LONG).show()
            onBackPressedDispatcher.onBackPressed()
            return
        }

        with(mMap) {
            uiSettings.apply {
                isScrollGesturesEnabled = true
                isZoomGesturesEnabled = true
            }
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        bottomSheetDialog()
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun bottomSheetDialog() {
        val bottomSheetGiatDetailBinding = BottomSheetGiatDetailBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this@GiatActivity).apply {
            setCancelable(true)
            setContentView(bottomSheetGiatDetailBinding.root)
        }
        with(mMap) {
            setOnMarkerClickListener { marker ->
                val idx = marker.title?.toIntOrNull() ?: return@setOnMarkerClickListener false
                val giat = listGiat[idx]

                with(bottomSheetGiatDetailBinding) {
                    tvGiatTujuan.text = getString(R.string.giat_tujuan_view, giat.tujuan)
                    tvGiatKeterangan.text = getString(R.string.keterangan_view, giat.keterangan)
                    tvGiatJenis.text = getString(R.string.jenis_view, giat.jenis)
                    tvGiatDepartemen.text = getString(R.string.giat_departemen_view, giat.departemen)
                    tvGiatLokasi.text = getString(R.string.giat_lokasi_view, giat.lokasi)
                    tvMulaiCuti.text = getString(R.string.mulai_view, giat.mulai)
                    tvSampaiCuti.text = getString(R.string.sampai_view, giat.sampai)
                    tvStatusGiat.text = getString(R.string.giat_proses_view, giat.proses)
                    if (giat.img?.isNotEmpty() == true) {
                        imgsliderGiat.visibility = View.VISIBLE
                        val imageList = java.util.ArrayList<SlideModel>()
                        for (img in giat.img) {
                            imageList.add(SlideModel(img.img))
                        }
                        imgsliderGiat.setImageList(imageList, ScaleTypes.CENTER_INSIDE)
                    } else {
                        imgsliderGiat.visibility = View.GONE
                    }
                }
                dialog.show()
                true
            }
        }
    }

    private fun getGiat(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getGiat("0", tokenAuth).enqueue(object : Callback<GiatResponse> {
            override fun onResponse(call: Call<GiatResponse>, response: Response<GiatResponse>) {
                if (response.isSuccessful) {
                    val giatResponse = response.body()
                    if (giatResponse?.status == "success") {
                        listGiat = giatResponse.data
                        for ((idx, data) in listGiat.withIndex()) {
                            val giatPos = data.posisi_giat[0].let { LatLng(it.lat.toDouble(), it.lng.toDouble()) }
                            drawMarker(idx.toString(), giatPos)
                        }
                    } else {
                        Toasty.info(this@GiatActivity, giatResponse?.message ?: "", Toasty.LENGTH_LONG).show()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@GiatActivity,
                        "getGiat | onResponse",
                        "Failed to retrieve GIAT data"
                    )
                }
            }

            override fun onFailure(call: Call<GiatResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@GiatActivity,
                    "getGiat | onFailure",
                    t.message.toString()
                )
            }
        })
    }

    private fun drawMarker(id: String, pos: LatLng) {
        val markerOptions = MarkerOptions()
            .position(pos)
            .title(id)
            .draggable(false)

        with(mMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12f))
            addMarker(markerOptions)
        }
    }
}