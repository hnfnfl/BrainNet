package com.jaylangkung.korem.giat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.dhaval2404.imagepicker.ImagePicker
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
import com.google.gson.Gson
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.DataSpinnerResponse
import com.jaylangkung.korem.dataClass.DefaultResponse
import com.jaylangkung.korem.dataClass.SpinnerDepartemenData
import com.jaylangkung.korem.databinding.ActivityTambahGiatBinding
import com.jaylangkung.korem.retrofit.AuthService
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.FileUtils
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
    private var userLat: Double? = null
    private var userLong: Double? = null
    private var giatLat: Double? = null
    private var giatLong: Double? = null
    private lateinit var mMap: GoogleMap

    private var photoUri: Uri? = null

    private var listDepartemen: ArrayList<SpinnerDepartemenData> = arrayListOf()
    private var iddepartemen: String = ""
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
                startActivity(Intent(this@TambahGiatActivity, GiatActivity::class.java))
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

            btnGiatFoto.setOnClickListener {
                ImagePicker.with(this@TambahGiatActivity)
                    .cropSquare() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                    .galleryMimeTypes( //Exclude gif images
                        mimeTypes = arrayOf(
                            "image/png",
                            "image/jpg",
                            "image/jpeg"
                        )
                    )
                    .start { resultCode, data ->
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                //Image Uri will not be null for RESULT_OK
                                val fileUri = data?.data
                                photoUri = fileUri
                                giatImgView.setImageURI(fileUri)
                                giatImgView.visibility = View.VISIBLE

                                //You can get File object from intent
                                ImagePicker.getFile(data)

                                //You can also get File Path from intent
                                ImagePicker.getFilePath(data).toString()
                            }
                            ImagePicker.RESULT_ERROR -> {
                                Toasty.error(this@TambahGiatActivity, ImagePicker.getError(data), Toasty.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.d("Cancel image picking", "Task Cancelled")
                            }
                        }
                    }
            }

            btnTambahGiat.setOnClickListener {
                if (validate()) {
                    val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

                    val latUser = userLat.toString()
                    val lngUser = userLong.toString()
                    val latGiat = if (giatLat != null) giatLat.toString() else userLat.toString()
                    val lngGiat = if (giatLong != null) giatLong.toString() else userLong.toString()
                    val userPos = arrayListOf(mapOf("lat" to latUser, "lng" to lngUser))
                    val giatPos = arrayListOf(mapOf("lat" to latGiat, "lng" to lngGiat))
                    val gson = Gson()
                    val userJson = gson.toJson(userPos).toString()
                    val giatJson = gson.toJson(giatPos).toString()

                    var photo: MultipartBody.Part? = null
                    photoUri?.let {
                        val file = FileUtils.getFile(this@TambahGiatActivity, photoUri)
                        val requestBodyPhoto = file?.asRequestBody(contentResolver.getType(it).toString().toMediaTypeOrNull())
                        photo = requestBodyPhoto?.let { it1 -> MultipartBody.Part.createFormData("foto_giat", file.name, it1) }
                    }

                    val tujuan = giatTujuanInput.text.toString()
                    val keterangan = giatKeteranganInput.text.toString()
                    val lokasi = giatLokasiInput.text.toString()

                    tambahGiat(
                        iduser.toRequestBody(MultipartBody.FORM),
                        iddepartemen.toRequestBody(MultipartBody.FORM),
                        jenisGiat.toRequestBody(MultipartBody.FORM),
                        tujuan.toRequestBody(MultipartBody.FORM),
                        keterangan.toRequestBody(MultipartBody.FORM),
                        dateStart.toRequestBody(MultipartBody.FORM),
                        dateEnd.toRequestBody(MultipartBody.FORM),
                        lokasi.toRequestBody(MultipartBody.FORM),
                        giatJson.toRequestBody(MultipartBody.FORM),
                        userJson.toRequestBody(MultipartBody.FORM),
                        photo,
                        tokenAuth
                    )
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
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

        client.lastLocation.addOnCompleteListener {
            Priority.PRIORITY_HIGH_ACCURACY
            userLat = it.result.latitude
            userLong = it.result.longitude
            val pos = LatLng(userLat!!, userLong!!)
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

        with(mMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f))
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
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
                                iddepartemen = listDepartemen[p2].iddepartemen
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

    private fun validate(): Boolean {
        binding.apply {
            when {
                giatTujuanInput.text.toString() == "" -> {
                    giatTujuanInput.error = "Judul Giat tidak boleh kosong"
                    giatTujuanInput.requestFocus()
                    return false
                }
                giatKeteranganInput.text.toString() == "" -> {
                    giatKeteranganInput.error = "Keterangan Giat tidak boleh kosong"
                    giatKeteranganInput.requestFocus()
                    return false
                }
                iddepartemen == "" -> {
                    Toasty.warning(this@TambahGiatActivity, "Departemen tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
                jenisGiat == "" -> {
                    Toasty.warning(this@TambahGiatActivity, "Jenis Giat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
                dateStart == "" -> {
                    Toasty.warning(this@TambahGiatActivity, "Tanggal mulai giat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
                dateEnd == "" -> {
                    Toasty.warning(this@TambahGiatActivity, "Tanggal selesai giat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
                giatLokasiInput.text.toString() == "" -> {
                    giatLokasiInput.error = "Lokasi Giat tidak boleh kosong"
                    giatLokasiInput.requestFocus()
                    return false
                }
            }
        }
        return true
    }

    private fun tambahGiat(
        iduser: RequestBody,
        iddepartemen: RequestBody,
        jenis: RequestBody,
        tujuan: RequestBody,
        keterangan: RequestBody,
        mulai: RequestBody,
        sampai: RequestBody,
        lokasi: RequestBody,
        posisiGiat: RequestBody,
        posisiAnggota: RequestBody,
        photo: MultipartBody.Part?,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertGiat(
            iduser,
            iddepartemen,
            jenis, tujuan,
            keterangan,
            mulai,
            sampai,
            "belum".toRequestBody(MultipartBody.FORM),
            lokasi,
            posisiGiat,
            posisiAnggota,
            photo,
            tokenAuth
        )
            .enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toasty.success(this@TambahGiatActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        ErrorHandler().responseHandler(
                            this@TambahGiatActivity,
                            "tambahGiat | onResponse", response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    binding.btnTambahGiat.endAnimation()
                    ErrorHandler().responseHandler(
                        this@TambahGiatActivity,
                        "tambahGiat | onFailure", t.message.toString()
                    )
                }

            })
    }

}