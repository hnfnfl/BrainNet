package com.jaylangkung.eoffice_korem.surat.masuk

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
import com.jaylangkung.eoffice_korem.MainActivity.Companion.listUserSurat
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityTambahSuratMasukBinding
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TambahSuratMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahSuratMasukBinding
    private lateinit var myPreferences: MySharedPreferences

    private var idPenerima: String = ""
    private var sumber: String = ""
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
                binding.suratMasukImgView.visibility = View.VISIBLE
                binding.suratMasukImgView.setImageURI(fileUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this@TambahSuratMasukActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("Cancel image picking", "Task Cancelled")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahSuratMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TambahSuratMasukActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@TambahSuratMasukActivity, SuratMasukActivity::class.java))
                finish()
            }
        })

        binding.apply {
            val listUser = ArrayList<String>()
            val listJenisSurat = ArrayList<String>()
            val listPerihalSurat = ArrayList<String>()
            for (i in 0 until listUserSurat.size) {
                listUser.add(listUserSurat[i].nama)
            }
            listJenisSurat.addAll(
                listOf("Militer", "Non Militer")
            )
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

            penerimaSpinner.item = listUser as List<*>?
            penerimaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    idPenerima = listUserSurat[p2].idsurat_user_aktivasi
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            sumberSuratSpinner.item = listJenisSurat as List<*>?
            sumberSuratSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    sumber = if (listJenisSurat[position] == "Non Militer") "non_militer" else "militer"
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            perihalSpinner.item = listPerihalSurat as List<*>?
            perihalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    perihal = listPerihalSurat[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnSuratMasukFoto.setOnClickListener {
                ImagePicker.with(this@TambahSuratMasukActivity).compress(1024).maxResultSize(1080, 1080).galleryMimeTypes(
                    arrayOf("image/png", "image/jpg", "image/jpeg")
                ).createIntent {
                    startForProfileImageResult.launch(it)
                }
            }

            btnTanggalSurat.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this@TambahSuratMasukActivity, { _, yearSelected, monthOfYear, dayOfMonth ->
                        tglSurat = getString(R.string.placeholder_date, yearSelected.toString(), (monthOfYear + 1).toString(), dayOfMonth.toString())
                        tvTglSuratMasuk.text = getString(R.string.tgl_sm, tglSurat)
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }

            btnTambahSuratMasuk.setOnClickListener {
                if (validate()) {
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                    val sumberNext = smSumberSuratNextInput.text.toString()
                    val pengirim = smPengirimInput.text.toString()
                    var foto: MultipartBody.Part? = null
                    photoUri?.let {
                        val file = FileUtils.getFile(this@TambahSuratMasukActivity, photoUri)
                        val requestBodyPhoto = file?.asRequestBody(contentResolver.getType(it).toString().toMediaTypeOrNull())
                        foto = requestBodyPhoto?.let { it1 -> MultipartBody.Part.createFormData("foto", file.name, it1) }
                    }

                    insertSuratMasuk(
                        idPenerima.toRequestBody(MultipartBody.FORM),
                        sumber.toRequestBody(MultipartBody.FORM),
                        sumberNext.toRequestBody(MultipartBody.FORM),
                        pengirim.toRequestBody(MultipartBody.FORM),
                        perihal.toRequestBody(MultipartBody.FORM),
                        tglSurat.toRequestBody(MultipartBody.FORM),
                        foto,
                        tokenAuth
                    )
                    idPenerima = ""
                    sumber = ""
                    perihal = ""
                    tglSurat = ""
                }
            }
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            when {
                idPenerima.isBlank() -> {
                    Toasty.warning(this@TambahSuratMasukActivity, "Penerima tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                    return false
                }
                sumber.isBlank() -> {
                    Toasty.warning(this@TambahSuratMasukActivity, "Sumber tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                    return false
                }
                perihal.isBlank() -> {
                    Toasty.warning(this@TambahSuratMasukActivity, "Perihal tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                    return false
                }
                smSumberSuratNextInput.text.toString().isBlank() -> {
                    smSumberSuratNextInput.error = "Sumber Surat tidak boleh kosong"
                    smSumberSuratNextInput.requestFocus()
                    return false
                }
                smPengirimInput.text.toString().isBlank() -> {
                    smPengirimInput.error = "Pengirim tidak boleh kosong"
                    smPengirimInput.requestFocus()
                    return false
                }
                else -> return true
            }
        }
    }

    private fun insertSuratMasuk(
        iduser: RequestBody,
        sumber: RequestBody,
        sumberNext: RequestBody,
        pengirim: RequestBody,
        perihal: RequestBody,
        tglSurat: RequestBody,
        file: MultipartBody.Part?,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.insertSuratMasuk(iduser, sumber, sumberNext, pengirim, perihal, tglSurat, file, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()!!.message
                    Toasty.success(this@TambahSuratMasukActivity, message, Toasty.LENGTH_SHORT).show()
                    startActivity(Intent(this@TambahSuratMasukActivity, SuratMasukActivity::class.java))
                    finish()
                } else {
                    ErrorHandler().responseHandler(
                        this@TambahSuratMasukActivity, "insertSuratMasuk | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@TambahSuratMasukActivity, "insertSuratMasuk | onFailure", t.message.toString()
                )
            }
        })
    }
}