package com.jaylangkung.eoffice_korem.surat.keluar

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jaylangkung.eoffice_korem.MainActivity.Companion.listUserSurat
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.databinding.ActivityTambahSuratKeluarBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.FileUtils
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

class TambahSuratKeluarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahSuratKeluarBinding
    private lateinit var myPreferences: MySharedPreferences

    private var idPenerima: String = ""
    private var perihal: String = ""
    private var tglSurat: String = ""

    private var photoUri: Uri? = null
    private var filePath: ArrayList<String> = ArrayList()

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                photoUri = fileUri
//                binding.suratKeluarImgView.visibility = View.VISIBLE
//                binding.suratKeluarImgView.setImageURI(fileUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this@TambahSuratKeluarActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("Cancel image picking", "Task Cancelled")
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val clipData = data.clipData

                if (clipData != null) {
                    // Multiple files were selected
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        processFile(convertUriToFilePath(this@TambahSuratKeluarActivity, uri)!!)
                    }
                } else {
                    // Single file was selected
                    val uri = data.data!!
                    processFile(convertUriToFilePath(this@TambahSuratKeluarActivity, uri)!!)
                }
            }
        }
    }

    private fun processFile(uri: Uri) {
        filePath.add(uri)

        val documentFile = DocumentFile.fromSingleUri(this@TambahSuratKeluarActivity, uri)
        val fileName = documentFile?.name
        Toast.makeText(this, "Selected file: $fileName", Toast.LENGTH_SHORT).show()

        // add layout to show file name to linear layout
        val file = TextView(this@TambahSuratKeluarActivity).apply {
            text = fileName
            setTextColor(ContextCompat.getColor(this@TambahSuratKeluarActivity, R.color.black))
            textSize = 16f
            setPadding(0, 12, 0, 0)
            val endIcon = ContextCompat.getDrawable(this@TambahSuratKeluarActivity, R.drawable.ic_close)
            setCompoundDrawablesWithIntrinsicBounds(null, null, endIcon, null)
            compoundDrawablePadding = 8
            // set on click listener on end icon
            setOnClickListener {
                binding.llSelectedFiles.removeView(this)
                filePath.remove(uri)
            }
        }
        binding.llSelectedFiles.addView(file)
    }

    private fun getPathFromContentUri(contentResolver: ContentResolver, uri: Uri): String? {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val fileNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (fileNameIndex != -1) {
                    return it.getString(fileNameIndex)
                }
            }
        }
        return null
    }

    private fun convertUriToFilePath(context: Context, originalUri: Uri): Uri? {
        val scheme = originalUri.scheme
        val authority = originalUri.authority
        val documentId = DocumentsContract.getDocumentId(originalUri)

        if (scheme == "content" && authority == "com.android.providers.media.documents") {
            val decodedDocumentId = Uri.decode(documentId)
            val split = decodedDocumentId.split(":")
            val type = split[0]
            val contentUri: Uri? = when (type) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> null
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            val column = "_data"
            val projection = arrayOf(column)

            contentUri?.let {
                context.contentResolver.query(it, projection, selection, selectionArgs, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(column)
                        val filePath = cursor.getString(columnIndex)
                        return Uri.fromFile(File(filePath))
                    }
                }
            }
        }

        return null
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

            btnSuratKeluarFiles.setOnClickListener {
//                ImagePicker.with(this@TambahSuratKeluarActivity).compress(1024).maxResultSize(1080, 1080).galleryMimeTypes(
//                    arrayOf("image/png", "image/jpg", "image/jpeg")
//                ).createIntent {
//                    startForProfileImageResult.launch(it)
//                }
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Enable multiple file selection
                }
                launcher.launch(intent)
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
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                    val kepada = skKepadaInput.text.toString()
                    val files = ArrayList<MultipartBody.Part>()
//                    var foto: MultipartBody.Part?
//                    photoUri?.let {
//                        val data = photoUri
//                        val file = FileUtils.getFile(this@TambahSuratKeluarActivity, photoUri)
//                        val requestBodyPhoto = file?.asRequestBody(contentResolver.getType(it).toString().toMediaTypeOrNull())
//                        foto = requestBodyPhoto?.let { it1 -> MultipartBody.Part.createFormData("foto", file.name, it1) }
//                        files.add(foto!!)
//                    }

                    for (uri in fileUri) {
                        val path = uri.path
                        if (path != null) {
                            val file = FileUtils.getFile(this@TambahSuratKeluarActivity, uri)
                            val requestFile = file?.asRequestBody(contentResolver.getType(uri).toString().toMediaTypeOrNull())
                            val part = requestFile?.let { it1 -> MultipartBody.Part.createFormData("files[]", file.name, it1) }
                            if (part != null) {
                                files.add(part)
                            }
                        } else {
                            Toasty.warning(this@TambahSuratKeluarActivity, "File tidak ditemukan").show()
                        }
                    }

                    insertSuratKeluar(
                        idPenerima.toRequestBody(MultipartBody.FORM),
                        perihal.toRequestBody(MultipartBody.FORM),
                        kepada.toRequestBody(MultipartBody.FORM),
                        idPenerima.toRequestBody(MultipartBody.FORM),
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
                    Toasty.warning(this@TambahSuratKeluarActivity, "Penerima tidak boleh kosong").show()
                    return false
                }
                perihal.isBlank() -> {
                    Toasty.warning(this@TambahSuratKeluarActivity, "Perihal tidak boleh kosong").show()
                    return false
                }
//                fileUri.size == 0 -> {
//                    Toasty.warning(this@TambahSuratKeluarActivity, "File tidak boleh kosong").show()
//                    return false
//                }
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
        iduser: RequestBody,
        perihal: RequestBody,
        kepada: RequestBody,
        persetujuan: RequestBody,
        tglSurat: RequestBody,
        files: ArrayList<MultipartBody.Part>,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.insertSuratKeluar(iduser, perihal, kepada, persetujuan, tglSurat, files, tokenAuth).enqueue(object : retrofit2.Callback<DefaultResponse> {
            override fun onResponse(call: retrofit2.Call<DefaultResponse>, response: retrofit2.Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()!!.message
                    this@TambahSuratKeluarActivity.idPenerima = ""
                    this@TambahSuratKeluarActivity.perihal = ""
                    this@TambahSuratKeluarActivity.tglSurat = ""
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