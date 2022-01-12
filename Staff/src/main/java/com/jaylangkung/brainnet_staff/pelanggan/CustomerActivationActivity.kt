package com.jaylangkung.brainnet_staff.pelanggan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityCustomerActivationBinding
import com.jaylangkung.brainnet_staff.pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.brainnet_staff.retrofit.ApiWilayah
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.WilayahService
import com.jaylangkung.brainnet_staff.retrofit.response.DataSpinnerResponse
import com.jaylangkung.brainnet_staff.retrofit.response.WilayahResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerActivationActivity : AppCompatActivity() {

    private lateinit var customerActivationBinding: ActivityCustomerActivationBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listPelanggan: ArrayList<DataSpinnerEntity> = arrayListOf()
    private var listSwitch: ArrayList<DataSpinnerEntity> = arrayListOf()
    private var listRekanan: ArrayList<DataSpinnerEntity> = arrayListOf()
    private var listPaketInstalasi: ArrayList<DataSpinnerEntity> = arrayListOf()

    private var idpelanggan: String = ""
    private var paket: String = ""
    private var isterminal: String = ""
    private var idswitch: String = ""
    private var idrekanan: String = ""
    private var idpaketinstalasi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerActivationBinding = ActivityCustomerActivationBinding.inflate(layoutInflater)
        setContentView(customerActivationBinding.root)
        myPreferences = MySharedPreferences(this@CustomerActivationActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getPelanggan()

        val listTerminal = ArrayList<String>()
        listTerminal.add("Tidak")
        listTerminal.add("Ya")
        customerActivationBinding.spinnerIsterminal.item = listTerminal as List<Any>?
        customerActivationBinding.spinnerIsterminal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                isterminal = listTerminal[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        customerActivationBinding.btnActivateCustomer.setOnClickListener {
            Log.e("logger", "idpelanggan : $idpelanggan, paket : $paket, isterminal : $isterminal")
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@CustomerActivationActivity, MainActivity::class.java))
        finish()
    }

    private fun getPelanggan() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getPelanggan().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listPelanggan.clear()
                    listPelanggan = response.body()!!.pelanggan
                    val list = ArrayList<String>()
                    for (i in 0 until listPelanggan.size) {
                        list.add(response.body()!!.pelanggan[i].nama)
                    }
                    customerActivationBinding.spinnerPelanggan.item = list as List<Any>?

                    customerActivationBinding.spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idpelanggan = listPelanggan[p2].idpelanggan
                            paket = listPelanggan[p2].paket
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@CustomerActivationActivity,
                        "getPelanggan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@CustomerActivationActivity,
                    "getPelanggan | onFailure", t.message.toString()
                )
            }
        })
    }
}