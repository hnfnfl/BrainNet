package com.jaylangkung.korem.pengaduan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.DefaultResponse
import com.jaylangkung.korem.databinding.ActivityTambahPengaduanBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.FileUtils
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahPengaduanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahPengaduanBinding
    private lateinit var myPreferences: MySharedPreferences

    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahPengaduanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TambahPengaduanActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@TambahPengaduanActivity, PengaduanActivity::class.java))
                finish()
            }
        })

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnPengaduanFoto.setOnClickListener {
                ImagePicker.with(this@TambahPengaduanActivity)
                    .cropSquare() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                    .galleryMimeTypes( //Exclude gif images
                        mimeTypes = arrayOf(
                            "image/png",
                            "image/jpg",
                            "image/jpeg"
                        )
                    )
                    .start { resultCode, data ->
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                //Image Uri will not be null for RESULT_OK
                                val fileUri = data?.data
                                photoUri = fileUri
                                pengaduanImgView.setImageURI(fileUri)
                                pengaduanImgView.visibility = View.VISIBLE

                                //You can get File object from intent
                                ImagePicker.getFile(data)

                                //You can also get File Path from intent
                                ImagePicker.getFilePath(data).toString()
                            }
                            ImagePicker.RESULT_ERROR -> {
                                Toasty.error(this@TambahPengaduanActivity, ImagePicker.getError(data), Toasty.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.d("Cancel image picking", "Task Cancelled")
                            }
                        }
                    }
            }

            btnTambahPengaduan.setOnClickListener {
                if (validate()) {
                    val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
                    val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

                    val judul = pengaduanJudulInput.text.toString()
                    val isi = pengaduanIsiInput.text.toString()

                    var photo: MultipartBody.Part? = null
                    photoUri?.let {
                        val file = FileUtils.getFile(this@TambahPengaduanActivity, photoUri)
                        val requestBodyPhoto = file?.asRequestBody(contentResolver.getType(it).toString().toMediaTypeOrNull())
                        photo = requestBodyPhoto?.let { it1 -> MultipartBody.Part.createFormData("foto_pengaduan", file.name, it1) }
                    }

                    tambahPengaduan(
                        iduser.toRequestBody(MultipartBody.FORM),
                        judul.toRequestBody(MultipartBody.FORM),
                        isi.toRequestBody(MultipartBody.FORM),
                        photo,
                        tokenAuth
                    )
                }
            }
        }
    }

    private fun validate(): Boolean {
        binding.apply {
            when {
                pengaduanJudulInput.text.toString() == "" -> {
                    pengaduanJudulInput.error = "Judul pengaduan tidak boleh kosong"
                    pengaduanJudulInput.requestFocus()
                    return false
                }
                pengaduanIsiInput.text.toString() == "" -> {
                    pengaduanIsiInput.error = "Isi pengaduan tidak boleh kosong"
                    pengaduanIsiInput.requestFocus()
                    return false
                }
            }
        }
        return true
    }

    private fun tambahPengaduan(
        iduser: RequestBody,
        judul: RequestBody,
        isi: RequestBody,
        photo: MultipartBody.Part?,
        tokenAuth: String
    ) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertPengaduan(iduser, judul, isi, photo, tokenAuth)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toasty.success(this@TambahPengaduanActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        ErrorHandler().responseHandler(
                            this@TambahPengaduanActivity,
                            "tambahGiat | onResponse", response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    binding.btnTambahPengaduan.endAnimation()
                    ErrorHandler().responseHandler(
                        this@TambahPengaduanActivity,
                        "insertPengaduan | onFailure", t.message.toString()
                    )
                }

            })
    }
}