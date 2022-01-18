package com.jaylangkung.brainnet_staff.menu_pelayanan.pembayaran

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityPembayaranBinding
import com.jaylangkung.brainnet_staff.menu_pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DataSpinnerResponse
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listTagihan: ArrayList<DataSpinnerEntity> = arrayListOf()

    private var idtagihan: String = ""
    private var total: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@PembayaranActivity)

        getSpinnerData()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@PembayaranActivity, MainActivity::class.java))
        finish()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listTagihan.clear()

                    listTagihan = response.body()!!.tagihan

                    val listA = ArrayList<String>()

                    for (i in 0 until listTagihan.size) {
                        listA.add("${response.body()!!.tagihan[i].nama}")
                    }

                    binding.spinnerPelanggan.item = listA as List<Any>?

                    binding.spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
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

}