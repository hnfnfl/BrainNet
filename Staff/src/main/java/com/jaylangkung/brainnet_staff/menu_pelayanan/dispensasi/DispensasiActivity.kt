package com.jaylangkung.brainnet_staff.menu_pelayanan.dispensasi

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityDispensasiBinding
import com.jaylangkung.brainnet_staff.menu_pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DataSpinnerResponse
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DispensasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDispensasiBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listPelanggan: ArrayList<DataSpinnerEntity> = arrayListOf()

    private var idpelanggan: String = ""
    private var tgl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDispensasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@DispensasiActivity)

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSpinnerData()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnTglJanji.setOnClickListener {
            val newCalendar: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this@DispensasiActivity,
                { _, year, monthOfYear, dayOfMonth ->
                    val newDate: Calendar = Calendar.getInstance()
                    newDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    tgl = dateFormatter.format(newDate.time)
                    binding.tvTglJanji.text = tgl
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.btnConfirmPayment.setOnClickListener {
            if (validate()) {
                insertDispensasi(idpelanggan, tgl, tokenAuth)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@DispensasiActivity, MainActivity::class.java))
        finish()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
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
                        this@DispensasiActivity,
                        "getSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@DispensasiActivity,
                    "getSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun validate(): Boolean {
        return when {
            idpelanggan == "" -> {
                Toasty.warning(this@DispensasiActivity, "Pelanggan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            tgl == "" -> {
                Toasty.warning(this@DispensasiActivity, "Tanggal Janji tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun insertDispensasi(
        idpelanggan: String,
        tanggal_janji: String,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertDispensasi(idpelanggan, tanggal_janji, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@DispensasiActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@DispensasiActivity,
                        "insertDispensasi | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@DispensasiActivity,
                    "insertDispensasi | onResponse", t.message.toString()
                )
            }
        })
    }
}