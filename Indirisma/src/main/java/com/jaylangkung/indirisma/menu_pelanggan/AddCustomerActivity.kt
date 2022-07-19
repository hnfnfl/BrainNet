package com.jaylangkung.indirisma.menu_pelanggan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityAddCustomerBinding
import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.WilayahEntity
import com.jaylangkung.indirisma.retrofit.*
import com.jaylangkung.indirisma.retrofit.response.DataSpinnerResponse
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.retrofit.response.WilayahResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listProvinsi: ArrayList<WilayahEntity> = arrayListOf()
    private var listKotaKab: ArrayList<WilayahEntity> = arrayListOf()
    private var listKecamatan: ArrayList<WilayahEntity> = arrayListOf()
    private var listKelurahan: ArrayList<WilayahEntity> = arrayListOf()
    private var listPaketInternet: ArrayList<DataSpinnerEntity> = arrayListOf()
    private var listMarketing: ArrayList<DataSpinnerEntity> = arrayListOf()
    private var listRekanan: ArrayList<DataSpinnerEntity> = arrayListOf()

    private var provinsi: String = ""
    private var kotaKab: String = ""
    private var kecamatan: String = ""
    private var kelurahan: String = ""
    private var paketInternet: String = ""
    private var marketing: String = ""
    private var rekanan: String = ""
    private var penagih: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@AddCustomerActivity)

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getProvinsi()
        getSpinnerData()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddCustomer.setOnClickListener {
            if (validate()) {
                val noktp = binding.customerInputKtp.text.toString()
                val namaPelanggan = binding.customerName.text.toString()
                val alamatPelanggan = binding.customerAddress.text.toString()
                val rt = binding.customerRt.text.toString()
                val rw = binding.customerRw.text.toString()
                val nohp = binding.customerPhone.text.toString()
                val alamatPasang = binding.customerAddressInstall.text.toString()
                val lokasi = binding.customerLocation.text.toString()

                val mDialog = MaterialDialog.Builder(this@AddCustomerActivity as Activity)
                    .setTitle("Tambahkan Pelanggan Baru")
                    .setMessage("Pastikan semua data sudah terisi dengan benar. Jika terjadi kesalahan silahkan hubungi Administrator")
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.yes), R.drawable.ic_add_user)
                    { dialogInterface, _ ->
                        insertPelanggan(
                            noktp,
                            namaPelanggan,
                            alamatPelanggan,
                            rt,
                            rw,
                            kelurahan,
                            kecamatan,
                            kotaKab,
                            provinsi,
                            nohp,
                            marketing,
                            alamatPasang,
                            paketInternet,
                            rekanan,
                            lokasi,
                            penagih,
                            tokenAuth
                        )
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(R.string.no), R.drawable.ic_close)
                    { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                // Show Dialog
                mDialog.show()
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@AddCustomerActivity, MainActivity::class.java))
        finish()
    }

    private fun getProvinsi() {
        val service = ApiWilayah().apiRequest().create(WilayahService::class.java)
        service.provinsi().enqueue(object : Callback<WilayahResponse> {
            override fun onResponse(call: Call<WilayahResponse>, response: Response<WilayahResponse>) {
                if (response.isSuccessful) {
                    listProvinsi.clear()
                    listProvinsi = response.body()!!.provinsi
                    val list = ArrayList<String>()
                    for (i in 0 until listProvinsi.size) {
                        list.add(response.body()!!.provinsi[i].nama)
                    }
                    list.sort()
                    listProvinsi.sortBy { it.nama }
                    binding.spinnerProvinsi.item = list as List<Any>?

                    binding.spinnerProvinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            listKotaKab.clear()
                            listKecamatan.clear()
                            binding.spinnerKecamatan.item = arrayListOf()
                            binding.spinnerKelurahan.item = arrayListOf()
                            provinsi = listProvinsi[p2].nama
                            getKotaKab(listProvinsi[p2].id)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@AddCustomerActivity,
                        "getProvinsi | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<WilayahResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@AddCustomerActivity,
                    "getProvinsi | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getKotaKab(id_provinsi: String) {
        val service = ApiWilayah().apiRequest().create(WilayahService::class.java)
        service.kotaKab(id_provinsi).enqueue(object : Callback<WilayahResponse> {
            override fun onResponse(call: Call<WilayahResponse>, response: Response<WilayahResponse>) {
                if (response.isSuccessful) {
                    listKotaKab = response.body()!!.kota_kabupaten
                    val list = ArrayList<String>()
                    for (i in 0 until listKotaKab.size) {
                        list.add(response.body()!!.kota_kabupaten[i].nama)
                    }
                    list.sort()
                    listKotaKab.sortBy { it.nama }
                    binding.spinnerKotaKab.item = list as List<Any>?

                    binding.spinnerKotaKab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            listKecamatan.clear()
                            listKelurahan.clear()
                            binding.spinnerKelurahan.item = arrayListOf()
                            kotaKab = listKotaKab[p2].nama
                            getkecamatan(listKotaKab[p2].id)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@AddCustomerActivity,
                        "getKotaKab | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<WilayahResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@AddCustomerActivity,
                    "getKotaKab | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getkecamatan(id_kota: String) {
        val service = ApiWilayah().apiRequest().create(WilayahService::class.java)
        service.kecamatan(id_kota).enqueue(object : Callback<WilayahResponse> {
            override fun onResponse(call: Call<WilayahResponse>, response: Response<WilayahResponse>) {
                if (response.isSuccessful) {
                    listKecamatan = response.body()!!.kecamatan
                    val list = ArrayList<String>()
                    for (i in 0 until listKecamatan.size) {
                        list.add(response.body()!!.kecamatan[i].nama)
                    }
                    list.sort()
                    listKecamatan.sortBy { it.nama }
                    binding.spinnerKecamatan.item = list as List<Any>?

                    binding.spinnerKecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            listKelurahan.clear()
                            kecamatan = listKecamatan[p2].nama
                            getKelurahan(listKecamatan[p2].id)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@AddCustomerActivity,
                        "getkecamatan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<WilayahResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@AddCustomerActivity,
                    "getkecamatan | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getKelurahan(id_kecamatan: String) {
        val service = ApiWilayah().apiRequest().create(WilayahService::class.java)
        service.kelurahan(id_kecamatan).enqueue(object : Callback<WilayahResponse> {
            override fun onResponse(call: Call<WilayahResponse>, response: Response<WilayahResponse>) {
                if (response.isSuccessful) {
                    listKelurahan = response.body()!!.kelurahan
                    val list = ArrayList<String>()
                    for (i in 0 until listKelurahan.size) {
                        list.add(response.body()!!.kelurahan[i].nama)
                    }
                    list.sort()
                    listKelurahan.sortBy { it.nama }
                    binding.spinnerKelurahan.item = list as List<Any>?

                    binding.spinnerKelurahan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            kelurahan = listKelurahan[p2].nama
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@AddCustomerActivity,
                        "getKelurahan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<WilayahResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@AddCustomerActivity,
                    "getKelurahan | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listPaketInternet.clear()
                    listMarketing.clear()
                    listRekanan.clear()

                    listPaketInternet = response.body()!!.paketInternet
                    listMarketing = response.body()!!.marketing
                    listRekanan = response.body()!!.rekanan

                    val listA = ArrayList<String>()
                    val listB = ArrayList<String>()
                    val listC = ArrayList<String>()
                    for (i in 0 until listPaketInternet.size) {
                        listA.add(response.body()!!.paketInternet[i].paket)
                    }
                    for (i in 0 until listMarketing.size) {
                        listB.add(response.body()!!.marketing[i].nama)
                    }
                    for (i in 0 until listRekanan.size) {
                        listC.add(response.body()!!.rekanan[i].nama)
                    }
                    binding.spinnerPaket.item = listA as List<Any>?
                    binding.spinnerMarketing.item = listB as List<Any>?
                    binding.spinnerPenagih.item = listB as List<Any>?
                    binding.spinnerRekanan.item = listC as List<Any>?


                    binding.spinnerPaket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            paketInternet = listPaketInternet[p2].idpaket_internet
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    binding.spinnerMarketing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            marketing = listMarketing[p2].idmarketing
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    binding.spinnerPenagih.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            penagih = listMarketing[p2].idmarketing
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    binding.spinnerRekanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            rekanan = listRekanan[p2].idrekanan
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@AddCustomerActivity,
                        "getSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@AddCustomerActivity,
                    "getSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun validate(): Boolean {
        when {
            binding.customerInputKtp.text.toString() == "" -> {
                binding.customerInputKtp.error = "Nomor KTP tidak boleh kosong"
                binding.customerInputKtp.requestFocus()
                return false
            }
            binding.customerName.text.toString() == "" -> {
                binding.customerName.error = "Nama Pelanggan tidak boleh kosong"
                binding.customerName.requestFocus()
                return false
            }
            binding.customerAddress.text.toString() == "" -> {
                binding.customerAddress.error = "Alamat Pelanggan tidak boleh kosong"
                binding.customerAddress.requestFocus()
                return false
            }
            binding.customerRt.text.toString() == "" -> {
                binding.customerRt.error = "RT tidak boleh kosong"
                binding.customerRt.requestFocus()
                return false
            }
            binding.customerRw.text.toString() == "" -> {
                binding.customerRw.error = "RW tidak boleh kosong"
                binding.customerRw.requestFocus()
                return false
            }
            binding.customerPhone.text.toString() == "" -> {
                binding.customerPhone.error = "Nomor HP tidak boleh kosong"
                binding.customerPhone.requestFocus()
                return false
            }
            binding.customerAddressInstall.text.toString() == "" -> {
                binding.customerAddressInstall.error = "Alamat Pemasangan tidak boleh kosong"
                binding.customerAddressInstall.requestFocus()
                return false
            }
            binding.customerLocation.text.toString() == "" -> {
                binding.customerLocation.error = "Lokasi tidak boleh kosong"
                binding.customerLocation.requestFocus()
                return false
            }
            provinsi == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Provinsi tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            kotaKab == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Kota/Kab tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            kecamatan == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Kecamatan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            kelurahan == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Kelurahan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            paketInternet == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Paket Internet tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            marketing == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Marketing tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            rekanan == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Rekanan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            penagih == "" -> {
                Toasty.warning(this@AddCustomerActivity, "Penagih tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun insertPelanggan(
        noktp: String,
        nama: String,
        alamat: String,
        rt: String,
        rw: String,
        kelurahan: String,
        kecamatan: String,
        kota: String,
        provinsi: String,
        nohp: String,
        idmarketing: String,
        alamat_pasang: String,
        paket: String,
        idrekanan: String,
        lokasi: String,
        penagih: String,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertPelanggan(
            noktp,
            nama,
            alamat,
            rt,
            rw,
            kelurahan,
            kecamatan,
            kota,
            provinsi,
            nohp,
            idmarketing,
            alamat_pasang,
            paket,
            idrekanan,
            lokasi,
            penagih,
            tokenAuth
        ).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@AddCustomerActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@AddCustomerActivity,
                        "insertPelanggan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@AddCustomerActivity,
                    "insertPelanggan | onResponse", t.message.toString()
                )
            }
        })
    }
}