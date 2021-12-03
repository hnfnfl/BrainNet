package com.jaylangkung.brainnet_staff.tiang

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityEditTiangBinding


class EditTiangActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var editTiangBinding: ActivityEditTiangBinding
    var currentMarker: Marker? = null
    lateinit var client: FusedLocationProviderClient
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
        editTiangBinding = ActivityEditTiangBinding.inflate(layoutInflater)
        setContentView(editTiangBinding.root)

        if (ContextCompat.checkSelfPermission(
                this@EditTiangActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@EditTiangActivity, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        } else {
            Toast.makeText(this, "Izin Lokasi diberikan", Toast.LENGTH_SHORT).show()
        }

        client = LocationServices.getFusedLocationProviderClient(this@EditTiangActivity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this@EditTiangActivity)
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
            latitude = it.result.latitude
            longitude = it.result.longitude
            editTiangBinding.tvLat.text = getString(R.string.latitude, latitude.toString())
            editTiangBinding.tvLng.text = getString(R.string.longitude, longitude.toString())
            val pos = LatLng(latitude!!, longitude!!)
            drawMarker(pos)
        }
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {}

            override fun onMarkerDragEnd(p0: Marker) {
                if (currentMarker != null)
                    currentMarker?.remove()

                newLatLng = p0.position
                editTiangBinding.tvLat.text = getString(R.string.latitude, newLatLng.latitude.toString())
                editTiangBinding.tvLng.text = getString(R.string.longitude, newLatLng.longitude.toString())
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
}