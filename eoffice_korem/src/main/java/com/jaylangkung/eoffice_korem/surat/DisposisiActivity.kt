package com.jaylangkung.eoffice_korem.surat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.eoffice_korem.MainActivity
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityDisposisiBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.surat.keluar.SuratKeluarActivity
import com.jaylangkung.eoffice_korem.surat.masuk.SuratMasukActivity
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DisposisiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisposisiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var activityIntent: Intent

    private var idPenerima: String = ""
    private var catatanTambahan: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisposisiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@DisposisiActivity)

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()
        val caller = intent.getStringExtra("caller").toString()
        val jenis = intent.getStringExtra("jenis").toString()
        val idsurat = intent.getStringExtra("idsurat").toString()

        activityIntent = if (caller == "surat_masuk") {
            Intent(this@DisposisiActivity, SuratMasukActivity::class.java)
        } else {
            Intent(this@DisposisiActivity, SuratKeluarActivity::class.java)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (caller) {
                    "surat_masuk" -> {
                        startActivity(activityIntent)
                    }
                    "surat_keluar" -> {
                        startActivity(activityIntent)
                    }
                    else -> {
                        Toasty.error(this@DisposisiActivity, "Caller Not Found", Toast.LENGTH_LONG).show()
                    }
                }
                finish()
            }
        })

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (jenis == "terusan") {
                catatanTambahanDisposisiCheckbox.visibility = View.GONE
                tvCatatanTambahan.visibility = View.GONE
                inputDisposisiCatatan.hint = "Catatan"
                btnSendDisposisi.progressText = "Teruskan"
                tvDisposisi.text = "Teruskan"
            } else {
                inputDisposisiCatatan.hint = "Disposisi"
                btnSendDisposisi.progressText = "Disposisi"
                tvDisposisi.text = "Disposisi"
            }

            val idPenerimaIDList = ArrayList<String>()
            for (i in 0 until MainActivity.listUserSurat.size) {
                val name = MainActivity.listUserSurat[i].nama
                val id = MainActivity.listUserSurat[i].idsurat_user_aktivasi
                val checkBox = CheckBox(this@DisposisiActivity)
                checkBox.text = name
                checkBox.tag = id
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        idPenerimaIDList.add(id)
                    } else {
                        idPenerimaIDList.remove(id)
                    }
                    idPenerima = idPenerimaIDList.joinToString(",")
                }
                penerimaDisposisiCheckbox.addView(checkBox)
            }


            val catatanTambahanIDList = listOf(
                checkboxAccCatat,
                checkboxIngatkan,
                checkboxKoordinasikan,
                checkboxLapor,
                checkboxMonitor,
                checkboxPedomani,
                checkboxSarankan,
                checkboxStSprinkanEdarkan,
                checkboxTindakLanjuti,
                checkboxUdkArsipkanMonitor,
                checkboxUdl,
                checkboxWakili
            )
            val checkedList = mutableListOf<String>()
            for (checkbox in catatanTambahanIDList) {
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    val checkBoxData = checkbox.text.toString().replace(" ", "_").lowercase(Locale.ROOT)
                    if (isChecked) {
                        checkedList.add(checkBoxData)
                    } else {
                        checkedList.remove(checkBoxData)
                    }
                    catatanTambahan = checkedList.joinToString(",")
                }
            }

            btnSendDisposisi.setOnClickListener {
                btnSendDisposisi.startAnimation()
                if (idPenerima != "") {
                    val catatan = inputDisposisiCatatan.text.toString()
                    insertSuratDisposisi(iduser, idsurat, caller, jenis, catatan, catatanTambahan, idPenerima, tokenAuth)
                    idPenerima = ""
                    catatanTambahan = ""
                } else {
                    btnSendDisposisi.endAnimation()
                    val errorMessage = if (jenis == "teruskan") {
                        "Penerima Terusan Tidak Boleh Kosong"
                    } else {
                        "Penerima Disposisi Tidak Boleh Kosong"
                    }
                    Toasty.warning(this@DisposisiActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun insertSuratDisposisi(
        iduser: String,
        idsurat: String,
        tipe_surat: String,
        jenis: String,
        catatan: String,
        catatanTambahan: String,
        penerima: String,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.insertSuratDisposisi(iduser, idsurat, tipe_surat, jenis, catatan, catatanTambahan, penerima, tokenAuth)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.status == "success") {
                            Toasty.success(this@DisposisiActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                            startActivity(activityIntent)
                            finish()
                        }
                    } else {
                        ErrorHandler().responseHandler(
                            this@DisposisiActivity,
                            "insertSuratDisposisi | onResponse", response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    ErrorHandler().responseHandler(
                        this@DisposisiActivity,
                        "insertSuratDisposisi | onFailure", t.message.toString()
                    )
                }
            })
    }

}