package com.jaylangkung.brainnet_staff.tiang

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityEditTiangBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTiangActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityEditTiangBinding
    private lateinit var myPreferences: MySharedPreferences
    var currentMarker: Marker? = null
    private lateinit var client: FusedLocationProviderClient
    lateinit var newLatLng: LatLng
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var mMap: GoogleMap

    companion object {
        const val idtiang = "idtiang"
        const val serialNumber = "serial number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTiangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@EditTiangActivity)

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val idtiang = intent.getStringExtra(idtiang).toString()



        client = LocationServices.getFusedLocationProviderClient(this@EditTiangActivity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this@EditTiangActivity)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            binding.btnSave.startAnimation()
            val keterangan = binding.tvValueDesc.text.toString()
            editTiang(idtiang, latitude.toString(), longitude.toString(), keterangan, tokenAuth)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@EditTiangActivity, ScannerTiangActivity::class.java))
        finish()
    }

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
            return
        }
        client.lastLocation.addOnCompleteListener {
            LocationRequest.PRIORITY_HIGH_ACCURACY
            latitude = it.result.latitude
            longitude = it.result.longitude
            binding.tvLat.text = getString(R.string.detail_tiang_latitude, latitude.toString())
            binding.tvLng.text = getString(R.string.detail_tiang_longitude, longitude.toString())
            val pos = LatLng(latitude!!, longitude!!)
            drawMarker(pos)
        }
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {}

            override fun onMarkerDragEnd(p0: Marker) {
                if (currentMarker != null)
                    currentMarker?.remove()

                newLatLng = p0.position
                binding.tvLat.text = getString(R.string.detail_tiang_latitude, newLatLng.latitude.toString())
                binding.tvLng.text = getString(R.string.detail_tiang_longitude, newLatLng.longitude.toString())
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker) {}
        })
    }

    private fun drawMarker(pos: LatLng) {
        val markerOptions = MarkerOptions()
            .position(pos)
            .title("Posisi Tiang")
            .draggable(true)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    private fun editTiang(idtiang: String, lat: String, lng: String, keterangan: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.editTiang(idtiang, lat, lng, keterangan, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.btnSave.endAnimation()
                        startActivity(Intent(this@EditTiangActivity, MainActivity::class.java))
                        Toasty.success(this@EditTiangActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        finish()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@EditTiangActivity,
                        "editTiang | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@EditTiangActivity,
                    "editTiang | onFailure", t.message.toString()
                )
            }
        })
    }
}