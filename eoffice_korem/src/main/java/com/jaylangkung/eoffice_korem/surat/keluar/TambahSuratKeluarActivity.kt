package com.jaylangkung.eoffice_korem.surat.keluar

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.eoffice_korem.MainActivity2.Companion.listUserSurat
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityTambahSuratKeluarBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.FileUtils
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import com.github.dhaval2404.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class TambahSuratKeluarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahSuratKeluarBinding
    private lateinit var myPreferences: MySharedPreferences

    private var idPenerima: String = ""
    private var perihal: String = ""
    private var tglSurat: String = ""
    private var photoUri: Uri? = null

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                photoUri = fileUri
                binding.suratKeluarImgView.visibility = View.VISIBLE
                binding.suratKeluarImgView.setImageURI(fileUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this@TambahSuratKeluarActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("Cancel image picking", "Task Cancelled")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahSuratKeluarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TambahSuratKeluarActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@TambahSuratKeluarActivity, SuratKeluarActivity::class.java))
                finish()
            }
        })

        binding.apply {
            val listUser = ArrayList<String>()
            val listPerihalSurat = ArrayList<String>()
            for (i in 0 until listUserSurat.size) {
                listUser.add(listUserSurat[i].nama)
            }
            listPerihalSurat.addAll(
                listOf(
                    "Biasa",
                    "Brafak",
                    "Bratel",
                    "DILMIL",
                    "Direktif",
                    "Hibah",
                    "KEP/SKEP",
                    "NHV",
                    "Nota Dinas",
                    "SPRIN",
                    "ST",
                    "STR",
                    "Surat Cuti",
                    "Surat Edaran",
                    "Telegram",
                    "Undangan"
                )
            )

            penerimaSpinner.item = listUser as List<Any>?
            penerimaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    idPenerima = listUserSurat[p2].idsurat_user_aktivasi
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            perihalSpinner.item = listPerihalSurat as List<Any>?
            perihalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    perihal = listPerihalSurat[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnSuratKeluarFoto.setOnClickListener {
                ImagePicker.with(this@TambahSuratKeluarActivity).compress(1024).maxResultSize(1080, 1080).galleryMimeTypes(
                    arrayOf("image/png", "image/jpg", "image/jpeg")
                ).createIntent {
                    startForProfileImageResult.launch(it)
                }
            }

            btnTanggalSurat.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this@TambahSuratKeluarActivity, { _, yearSelected, monthOfYear, dayOfMonth ->
                        tglSurat = getString(R.string.placeholder_date, yearSelected.toString(), (monthOfYear + 1).toString(), dayOfMonth.toString())
                        tvTglSuratKeluar.text = getString(R.string.tgl_sm, tglSurat)
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }

            btnTambahSuratKeluar.setOnClickListener {
                if (validate()) {
                    val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                    val kepada = skKepadaInput.text.toString()
                    var foto: MultipartBody.Part? = null
                    photoUri?.let {
                        val file = FileUtils.getFile(this@TambahSuratKeluarActivity, photoUri)
                        val requestBodyPhoto = file?.asRequestBody(contentResolver.getType(it).toString().toMediaTypeOrNull())
                        foto = requestBodyPhoto?.let { it1 -> MultipartBody.Part.createFormData("foto", file.name, it1) }
                    }

                    insertSuratKeluar(
                        iduser.toRequestBody(MultipartBody.FORM),
                        perihal.toRequestBody(MultipartBody.FORM),
                        kepada.toRequestBody(MultipartBody.FORM),
                        idPenerima.toRequestBody(MultipartBody.FORM),
                        tglSurat.toRequestBody(MultipartBody.FORM),
                        foto,
                        tokenAuth
                    )
                }
            }
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            when {
                idPenerima.isBlank() -> {
                    Toasty.warning(this@TambahSuratKeluarActivity, "Penerima tidak boleh kosong").show()
                    return false
                }
                perihal.isBlank() -> {
                    Toasty.warning(this@TambahSuratKeluarActivity, "Perihal tidak boleh kosong").show()
                    return false
                }
                skKepadaInput.text.toString().isBlank() -> {
                    skKepadaInput.error = "Kepada tidak boleh kosong"
                    skKepadaInput.requestFocus()
                    return false
                }
                else -> return true
            }
        }
    }

    private fun insertSuratKeluar(
        iduser: RequestBody, perihal: RequestBody, kepada: RequestBody, persetujuan: RequestBody, tglSurat: RequestBody, file: MultipartBody.Part?, tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.insertSuratKeluar(iduser, perihal, kepada, persetujuan, tglSurat, file, tokenAuth).enqueue(object : retrofit2.Callback<DefaultResponse> {
            override fun onResponse(call: retrofit2.Call<DefaultResponse>, response: retrofit2.Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()!!.message
                    Toasty.success(this@TambahSuratKeluarActivity, message, Toasty.LENGTH_SHORT).show()
                    startActivity(Intent(this@TambahSuratKeluarActivity, SuratKeluarActivity::class.java))
                    finish()
                } else {
                    ErrorHandler().responseHandler(
                        this@TambahSuratKeluarActivity, "insertSuratKeluar | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@TambahSuratKeluarActivity, "insertSuratKeluar | onFailure", t.message.toString()
                )
            }
        })
    }
}