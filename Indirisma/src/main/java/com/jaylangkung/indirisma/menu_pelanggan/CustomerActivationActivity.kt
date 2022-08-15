package com.jaylangkung.indirisma.menu_pelanggan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityCustomerActivationBinding
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

class CustomerActivationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerActivationBinding
    private lateinit var myPreferences: MySharedPreferences

    private var listBelumAktif: ArrayList<DataSpinnerEntity> = arrayListOf()
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
        binding = ActivityCustomerActivationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@CustomerActivationActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSpinnerData()

        val listTerminal = ArrayList<String>()
        listTerminal.add("Tidak")
        listTerminal.add("Ya")

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }
            spinnerIsterminal.item = listTerminal as List<Any>?
            spinnerIsterminal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    isterminal = listTerminal[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

            btnActivateCustomer.setOnClickListener {
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

    }

    override fun onBackPressed() {
        startActivity(Intent(this@CustomerActivationActivity, MainActivity::class.java))
        finish()
    }

    private fun getSpinnerData() {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.getSpinnerData("true").enqueue(object : Callback<DataSpinnerResponse> {
            override fun onResponse(call: Call<DataSpinnerResponse>, response: Response<DataSpinnerResponse>) {
                if (response.isSuccessful) {
                    listBelumAktif.clear()
                    listSwitch.clear()
                    listRekanan.clear()
                    listPaketInstalasi.clear()

                    listBelumAktif = response.body()!!.belumAktif
                    listSwitch = response.body()!!.switch
                    listRekanan = response.body()!!.rekanan
                    listPaketInstalasi = response.body()!!.paketInstalasi

                    val listA = ArrayList<String>()
                    val listB = ArrayList<String>()
                    val listC = ArrayList<String>()
                    val listD = ArrayList<String>()
                    for (i in 0 until listBelumAktif.size) {
                        listA.add("${i + 1}. ${response.body()!!.belumAktif[i].nama}")
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

                    binding.spinnerPelanggan.item = listA as List<Any>?
                    binding.spinnerSwitch.item = listB as List<Any>?
                    binding.spinnerRekanan.item = listC as List<Any>?
                    binding.spinnerPaketInstalasi.item = listD as List<Any>?

                    binding.spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idpelanggan = listBelumAktif[p2].idpelanggan
                            paket = listBelumAktif[p2].paket
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    binding.spinnerSwitch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idswitch = listSwitch[p2].idswitch
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    binding.spinnerRekanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            idrekanan = listRekanan[p2].idrekanan
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}

                    }

                    binding.spinnerPaketInstalasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        return when {
            idpelanggan == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Pelanggan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            isterminal == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Silahkan pilih apakah terminal atau tidak", Toasty.LENGTH_SHORT).show()
                false
            }
            idswitch == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Switch tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            idrekanan == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Rekanan tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                false
            }
            idpaketinstalasi == "" -> {
                Toasty.warning(this@CustomerActivationActivity, "Paket Instalasi tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                return false
            }
            else -> true
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
            idpelanggan, paket, idadmin, isterminal, idswitch, idrekanan, idpaket_instalasi, tokenAuth, "true"
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