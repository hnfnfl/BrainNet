package com.jaylangkung.brainnet_staff.pelanggan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityCustomerActivationBinding
import com.jaylangkung.brainnet_staff.pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DataSpinnerResponse
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
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

        getSpinnerData()

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

        customerActivationBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        customerActivationBinding.btnActivateCustomer.setOnClickListener {
            if (validate()) {
                val mDialog = MaterialDialog.Builder(this@CustomerActivationActivity as Activity)
                    .setTitle("Aktivasi Pelanggan Baru")
                    .setMessage("Pastikan semua data sudah terisi dengan benar. Jika terjadi kesalahan silahkan hubungi Administrator")
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.yes), R.drawable.ic_check_user)
                    { dialogInterface, _ ->
                        insertAktivasi(idpelanggan, paket, idadmin, isterminal, idswitch, idrekanan, idpaketinstalasi, tokenAuth)
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
        startActivity(Intent(this@CustomerActivationActivity, MainActivity::class.java))
        finish()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData().enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listPelanggan.clear()
                    listSwitch.clear()
                    listRekanan.clear()
                    listPaketInstalasi.clear()

                    listPelanggan = response.body()!!.pelanggan
                    listSwitch = response.body()!!.switch
                    listRekanan = response.body()!!.rekanan
                    listPaketInstalasi = response.body()!!.paketInstalasi

                    val listA = ArrayList<String>()
                    val listB = ArrayList<String>()
                    val listC = ArrayList<String>()
                    val listD = ArrayList<String>()
                    for (i in 0 until listPelanggan.size) {
                        listA.add(response.body()!!.pelanggan[i].nama)
                    }
                    for (i in 0 until listSwitch.size) {
                        listB.add(response.body()!!.switch[i].nomer)
                    }
                    for (i in 0 until listRekanan.size) {
                        listC.add(response.body()!!.rekanan[i].nama)
                    }
                    for (i in 0 until listPaketInstalasi.size) {
                        listD.add(response.body()!!.paketInstalasi[i].paket_instalasi)
                    }

                    customerActivationBinding.spinnerPelanggan.item = listA as List<Any>?
                    customerActivationBinding.spinnerSwitch.item = listB as List<Any>?
                    customerActivationBinding.spinnerRekanan.item = listC as List<Any>?
                    customerActivationBinding.spinnerPaketInstalasi.item = listD as List<Any>?

                    customerActivationBinding.spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idpelanggan = listPelanggan[p2].idpelanggan
                            paket = listPelanggan[p2].paket
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    customerActivationBinding.spinnerSwitch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idswitch = listSwitch[p2].idswitch
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    customerActivationBinding.spinnerRekanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idrekanan = listRekanan[p2].idrekanan
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    customerActivationBinding.spinnerPaketInstalasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idpaketinstalasi = listPaketInstalasi[p2].idpaket_instalasi
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@CustomerActivationActivity,
                        "getSpinnerData | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DataSpinnerResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@CustomerActivationActivity,
                    "getSpinnerData | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun validate(): Boolean {
        when {
            idpelanggan == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Pelanggan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            isterminal == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Silahkan pilih apakah terminal atau tidak", Toasty.LENGTH_SHORT).show()
                return false
            }
            idswitch == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Switch tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            idrekanan == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Rekanan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            idpaketinstalasi == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Paket Instalasi tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun insertAktivasi(
        idpelanggan: String,
        paket: String,
        idadmin: String,
        isterminal: String,
        idswitch: String,
        idrekanan: String,
        idpaket_instalasi: String,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertAktivasi(
            idpelanggan, paket, idadmin, isterminal, idswitch, idrekanan, idpaket_instalasi, tokenAuth
        ).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@CustomerActivationActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@CustomerActivationActivity,
                        "insertAktivasi | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@CustomerActivationActivity,
                    "insertAktivasi | onResponse", t.message.toString()
                )
            }
        })
    }
}