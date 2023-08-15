package com.jaylangkung.eoffice_korem.surat.masuk

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.jaylangkung.eoffice_korem.MainActivity.Companion.listUserSurat
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityTambahSuratMasukBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import com.jaylangkung.eoffice_korem.utils.convertFilesToMultipart
import es.dmoral.toasty.Toasty
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TambahSuratMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahSuratMasukBinding
    private lateinit var myPreferences: MySharedPreferences

    private var idPenerima: String = ""
    private var jenis: String = ""
    private var perihal: String = ""
    private var tglSurat: String = ""
    private var fileUri: ArrayList<Uri> = ArrayList()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val clipData = data.clipData

                if (clipData != null) {
                    // Multiple files were selected
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        processFile(uri)
                    }
                } else {
                    // Single file was selected
                    val uri = data.data!!
                    processFile(uri)
                }
            }
        }
    }

    private fun processFile(uri: Uri) {
        fileUri.add(uri)

        val fileName = DocumentFile.fromSingleUri(this@TambahSuratMasukActivity, uri)?.name
        // add layout to show file name to linear layout
        val file = TextView(this@TambahSuratMasukActivity).apply {
            text = fileName
            setTextColor(ContextCompat.getColor(this@TambahSuratMasukActivity, R.color.black))
            textSize = 16f
            setPadding(0, 12, 0, 0)
            val endIcon = ContextCompat.getDrawable(this@TambahSuratMasukActivity, R.drawable.ic_close)
            setCompoundDrawablesWithIntrinsicBounds(null, null, endIcon, null)
            compoundDrawablePadding = 8
            // set on click listener on end icon
            setOnClickListener {
                binding.llSelectedFiles.removeView(this)
                fileUri.remove(uri)
            }
        }
        binding.llSelectedFiles.addView(file)
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
                    idPenerima = listUserSurat[p2].idsuratUserAktivasi
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            sumberSuratSpinner.item = listJenisSurat as List<*>?
            sumberSuratSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    jenis = if (listJenisSurat[position] == "Non Militer") "non_militer" else "militer"
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
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Enable multiple file selection
                }
                launcher.launch(intent)
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
                    binding.btnTambahSuratMasuk.startAnimation()
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                    val sumberNext = smSumberSuratNextInput.text.toString()
                    val pengirim = smPengirimInput.text.toString()
                    val files = convertFilesToMultipart(fileUri, contentResolver)

                    insertSuratMasuk(
                        idPenerima.toRequestBody(MultipartBody.FORM),
                        jenis.toRequestBody(MultipartBody.FORM),
                        sumberNext.toRequestBody(MultipartBody.FORM),
                        pengirim.toRequestBody(MultipartBody.FORM),
                        perihal.toRequestBody(MultipartBody.FORM),
                        tglSurat.toRequestBody(MultipartBody.FORM),
                        files,
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
                    Toasty.warning(this@TambahSuratMasukActivity, "Penerima tidak boleh kosong", Toasty.LENGTH_SHORT).show()
                    return false
                }
                jenis.isBlank() -> {
                    Toasty.warning(this@TambahSuratMasukActivity, "Jenis Surat tidak boleh kosong", Toasty.LENGTH_SHORT).show()
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
                fileUri.size == 0 -> {
                    Toasty.warning(this@TambahSuratMasukActivity, "Foto tidak boleh kosong", Toasty.LENGTH_SHORT).show()
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
        file: ArrayList<MultipartBody.Part>,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.insertSuratMasuk(iduser, sumber, sumberNext, pengirim, perihal, tglSurat, file, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                binding.btnTambahSuratMasuk.endAnimation()
                if (response.isSuccessful) {
                    val message = response.body()!!.message
                    this@TambahSuratMasukActivity.idPenerima = ""
                    this@TambahSuratMasukActivity.jenis = ""
                    this@TambahSuratMasukActivity.perihal = ""
                    this@TambahSuratMasukActivity.tglSurat = ""
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
                binding.btnTambahSuratMasuk.endAnimation()
                ErrorHandler().responseHandler(
                    this@TambahSuratMasukActivity, "insertSuratMasuk | onFailure", t.message.toString()
                )
            }
        })
    }
}