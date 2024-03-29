package com.jaylangkung.korem.cuti

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.DefaultResponse
import com.jaylangkung.korem.databinding.ActivityTambahCutiBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TambahCutiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahCutiBinding
    private lateinit var myPreferences: MySharedPreferences

    private var jenisCuti: String = ""
    private var dateStart: String = ""
    private var dateEnd: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahCutiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TambahCutiActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@TambahCutiActivity, CutiActivity::class.java))
                finish()
            }
        })

        val listJenisCuti = ArrayList<String>()
        listJenisCuti.addAll(
            listOf(
                "Cuti Sakit",
                "Cuti Tahunan",
                "Cuti Dinas Lama",
                "Cuti Kawin",
                "Cuti Luar Biasa",
                "Cuti Istimewa",
                "Cuti Ibadah Haji/Umroh",
                "Cuti Lainnya"
            )
        )


        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            tvTglMulai.text = getString(R.string.tv_tgl_mulai, "")
            tvTglSelesai.text = getString(R.string.tv_tgl_selesai, "")

            btnDateMulai.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@TambahCutiActivity, { _, yearSelected, monthOfYear, dayOfMonth ->
                        dateStart = getString(R.string.placeholder_date, yearSelected.toString(), (monthOfYear + 1).toString(), dayOfMonth.toString())
                        tvTglMulai.text = getString(R.string.tv_tgl_mulai, dateStart)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            btnDateSampai.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@TambahCutiActivity, { _, yearSelected, monthOfYear, dayOfMonth ->
                        dateEnd = getString(R.string.placeholder_date, yearSelected.toString(), (monthOfYear + 1).toString(), dayOfMonth.toString())
                        tvTglSelesai.text = getString(R.string.tv_tgl_selesai, dateEnd)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            spinnerJenisCuti.item = listJenisCuti as List<Any>?
            spinnerJenisCuti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    jenisCuti = listJenisCuti[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

            btnAjukanCuti.setOnClickListener {
                if (validate()) {
                    val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                    val judulCuti = tvValueCutiJudul.text.toString()
                    val keteranganCuti = tvValueCutiKeterangan.text.toString()
                    val tglMulai = dateStart
                    val tglSelesai = dateEnd
                    val jenisCuti = jenisCuti
                    pengajuanPermohonanCuti(iduser, judulCuti, keteranganCuti, tglMulai, tglSelesai, jenisCuti, tokenAuth)
                }
            }
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            when {
                tvValueCutiJudul.text.toString() == "" -> {
                    tvValueCutiJudul.error = "Judul Cuti tidak boleh kosong"
                    tvValueCutiJudul.requestFocus()
                    return false
                }
                jenisCuti == "" -> {
                    Toasty.warning(this@TambahCutiActivity, "Jenis cuti tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
                dateStart == "" -> {
                    Toasty.warning(this@TambahCutiActivity, "Tanggal mulai cuti tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
                dateEnd == "" -> {
                    Toasty.warning(this@TambahCutiActivity, "Tanggal selesai cuti tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun pengajuanPermohonanCuti(iduser: String, judul: String, keterangan: String, mulai: String, selesai: String, jenis: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertCuti(iduser, judul, jenis, keterangan, mulai, selesai, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    Toasty.success(this@TambahCutiActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    ErrorHandler().responseHandler(
                        this@TambahCutiActivity,
                        "pengajuanPermohonanCuti | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.btnAjukanCuti.endAnimation()
                ErrorHandler().responseHandler(
                    this@TambahCutiActivity,
                    "pengajuanPermohonanCuti | onFailure", t.message.toString()
                )
            }

        })
    }
}