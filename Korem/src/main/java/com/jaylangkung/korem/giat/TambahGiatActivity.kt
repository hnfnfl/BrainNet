package com.jaylangkung.korem.giat

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.DataSpinnerResponse
import com.jaylangkung.korem.dataClass.SpinnerDepartemenData
import com.jaylangkung.korem.databinding.ActivityTambahGiatBinding
import com.jaylangkung.korem.retrofit.AuthService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TambahGiatActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityTambahGiatBinding
    private lateinit var myPreferences: MySharedPreferences

    var currentMarker: Marker? = null
    private lateinit var client: FusedLocationProviderClient
    lateinit var newLatLng: LatLng
    private var giatLat: Double? = null
    private var giatLong: Double? = null
    private lateinit var mMap: GoogleMap

    private var listDepartemen: ArrayList<SpinnerDepartemenData> = arrayListOf()
    private var departemen: String = ""
    private var jenisGiat: String = ""
    private var dateStart: String = ""
    private var dateEnd: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahGiatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TambahGiatActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                startActivity(Intent(this@TambahGiatActivity, CutiActivity::class.java))
                finish()
            }
        })

        client = LocationServices.getFusedLocationProviderClient(this@TambahGiatActivity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.giat_map) as SupportMapFragment
        mapFragment.getMapAsync(this@TambahGiatActivity)

        val listJenisGiat = ArrayList<String>()
        listJenisGiat.addAll(
            listOf(
                "BHAKTI",
                "KOMSOS",
                "PUANTER",
                "WANWIL",
            )
        )

        getSpinnerData()

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnGiatMulai.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@TambahGiatActivity, { _, yearSelected, monthOfYear, dayOfMonth ->
                        dateStart = getString(R.string.placeholder_date, yearSelected.toString(), (monthOfYear + 1).toString(), dayOfMonth.toString())
                        tvTglMulai.text = getString(R.string.giat_mulai_view, dateStart)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            btnGiatSampai.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@TambahGiatActivity, { _, yearSelected, monthOfYear, dayOfMonth ->
                        dateEnd = getString(R.string.placeholder_date, yearSelected.toString(), (monthOfYear + 1).toString(), dayOfMonth.toString())
                        tvTglSelesai.text = getString(R.string.giat_selesai_view, dateEnd)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            giatJenisSpinner.item = listJenisGiat as List<Any>?
            giatJenisSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    jenisGiat = listJenisGiat[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
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
            return
        }
        client.lastLocation.addOnCompleteListener {
            Priority.PRIORITY_HIGH_ACCURACY
            giatLat = it.result.latitude
            giatLong = it.result.longitude
            val pos = LatLng(giatLat!!, giatLong!!)
            Log.e("posisi user", "$giatLat, $giatLong")
            drawMarker(pos)
        }
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {}

            override fun onMarkerDragEnd(p0: Marker) {
                if (currentMarker != null)
                    currentMarker?.remove()

                newLatLng = p0.position
                giatLat = newLatLng.latitude
                giatLong = newLatLng.longitude
                Log.e("posisi user setelah geser", "$giatLat, $giatLong")
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker) {}
        })
    }

    private fun drawMarker(pos: LatLng) {
        val markerOptions = MarkerOptions()
            .position(pos)
            .title("Posisi Giat")
            .draggable(true)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listDepartemen.clear()

                    listDepartemen = response.body()!!.departemen
                    val listDept = ArrayList<String>()
                    for (i in 0 until listDepartemen.size) {
                        listDept.add(listDepartemen[i].departemen)
                    }
                    binding.apply {
                        giatDepartemenSpinner.item = listDept as List<Any>?

                        giatDepartemenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                departemen = listDepartemen[p2].iddepartemen
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {}
                        }
                    }

                } else {
                    ErrorHandler().responseHandler(
                        this@TambahGiatActivity,
                        "getSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@TambahGiatActivity,
                    "getSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }
}