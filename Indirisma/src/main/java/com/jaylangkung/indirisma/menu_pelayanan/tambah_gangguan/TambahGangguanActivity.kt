package com.jaylangkung.indirisma.menu_pelayanan.tambah_gangguan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityTambahGangguanBinding
import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.indirisma.retrofit.AuthService
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DataSpinnerResponse
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahGangguanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahGangguanBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listPelanggan: ArrayList<DataSpinnerEntity> = arrayListOf()

    private var idpelanggan: String = ""
    private var kepada: String = ""
    private var prioritas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahGangguanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TambahGangguanActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSpinnerData()

        val listKepada = ArrayList<String>()
        listKepada.add("Teknisi")
        listKepada.add("Billing")
        listKepada.add("Jaringan")
        listKepada.add("Administrasi")
        listKepada.add("Dispensasi")
        listKepada.add("Lainnya")

        val listPrioritas = ArrayList<String>()
        listPrioritas.add("Low")
        listPrioritas.add("Medium")
        listPrioritas.add("High")
        listPrioritas.add("Urgent")

        binding.apply {
            spinnerKepada.item = listKepada as List<Any>?
            spinnerPrioritas.item = listPrioritas as List<Any>?

            spinnerKepada.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    kepada = listKepada[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

            spinnerPrioritas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    prioritas = listPrioritas[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

            btnBack.setOnClickListener {
                onBackPressed()
            }

            btnAddGangguan.setOnClickListener {
                if (validate()) {
                    val isi = tvTambahGangguanIsi.text.toString()
                    insertGangguan(idpelanggan, kepada, prioritas, isi, idadmin, tokenAuth)
                }
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@TambahGangguanActivity, MainActivity::class.java))
        finish()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData("true").enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listPelanggan.clear()

                    listPelanggan = response.body()!!.pelanggan

                    val listA = ArrayList<String>()

                    for (i in 0 until listPelanggan.size) {
                        listA.add(response.body()!!.pelanggan[i].nama)
                    }

                    binding.spinnerPelanggan.item = listA as List<Any>?

                    binding.spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idpelanggan = listPelanggan[p2].idpelanggan
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@TambahGangguanActivity,
                        "getSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@TambahGangguanActivity,
                    "getSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun validate(): Boolean {
        return when {
            idpelanggan == "" -> {
                Toasty.warning(this@TambahGangguanActivity, "Pelanggan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            kepada == "" -> {
                Toasty.warning(this@TambahGangguanActivity, "Kepada tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            prioritas == "" -> {
                Toasty.warning(this@TambahGangguanActivity, "Prioritas tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            binding.tvTambahGangguanIsi.text.toString() == "" -> {
                binding.tvTambahGangguanIsi.error = "Isi tidak boleh kosong"
                binding.tvTambahGangguanIsi.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun insertGangguan(
        idpelanggan: String,
        kepada: String,
        prioritas: String,
        isi: String,
        idadmin: String,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertGangguan(
            idpelanggan, kepada, prioritas, isi, idadmin, tokenAuth, "true"
        ).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@TambahGangguanActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@TambahGangguanActivity,
                        "insertGangguan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@TambahGangguanActivity,
                    "insertGangguan | onFailure", t.message.toString()
                )
            }
        })
    }
}