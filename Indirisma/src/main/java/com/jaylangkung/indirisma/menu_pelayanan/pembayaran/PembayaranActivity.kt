package com.jaylangkung.indirisma.menu_pelayanan.pembayaran

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityPembayaranBinding
import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.indirisma.retrofit.AuthService
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DataSpinnerResponse
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listTagihan: ArrayList<DataSpinnerEntity> = arrayListOf()

    private var nama: String = ""
    private var idtagihan: String = ""
    private var total: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@PembayaranActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSpinnerData()

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }

            btnConfirmPayment.setOnClickListener {
                if (validate()) {
                    val mDialog = MaterialDialog.Builder(this@PembayaranActivity as Activity)
                        .setTitle("Konfirmasi Pembayaran")
                        .setMessage("Konfirmasikan pembayaran pelanggan atas nama $nama?")
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes), R.drawable.ic_pay)
                        { dialogInterface, _ ->
                            insertPembayaran(idtagihan, total, idadmin, tokenAuth)
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

    }

    override fun onBackPressed() {
        startActivity(Intent(this@PembayaranActivity, MainActivity::class.java))
        finish()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData("true").enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listTagihan.clear()

                    listTagihan = response.body()!!.tagihan

                    val listA = ArrayList<String>()

                    for (i in 0 until listTagihan.size) {
                        val text = "$i. ${response.body()!!.tagihan[i].nama}"
                        listA.add(text)
                    }

                    binding.spinnerPelanggan.item = listA as List<Any>?

                    binding.spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            nama = listTagihan[p2].nama
                            idtagihan = listTagihan[p2].idtagihan
                            total = listTagihan[p2].total
                            binding.paymentName.text = getString(R.string.pembayaran_nama, listTagihan[p2].nama)
                            binding.paymentInvoice.text = getString(R.string.pembayaran_invoice, listTagihan[p2].no_invoice)
                            val formatter = DecimalFormat("#,###.#")
                            binding.paymentTotal.text = getString(
                                R.string.pembayaran_tagihan,
                                formatter.format(listTagihan[p2].total.toFloat())
                            )
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@PembayaranActivity,
                        "getSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@PembayaranActivity,
                    "getSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun validate(): Boolean {
        return if (idtagihan == "") {
            Toasty.warning(this@PembayaranActivity, "Pelanggan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
            false
        } else true
    }

    private fun insertPembayaran(
        idtagihan: String,
        jumlah_tagihan: String,
        idadmin: String,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertPembayaran(idtagihan, jumlah_tagihan, idadmin, tokenAuth, "true").enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@PembayaranActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@PembayaranActivity,
                        "insertPembayaran | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@PembayaranActivity,
                    "insertPembayaran | onResponse", t.message.toString()
                )
            }
        })
    }
}