package com.jaylangkung.brainnet_staff.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.DefaultResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityEditProfileBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.FileUtils
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var myPreferences: MySharedPreferences
    private var photoUri: Uri? = null

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                photoUri = fileUri
                binding.imgProfile.setImageURI(fileUri)
            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this@EditProfileActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }

            else -> {
                Log.d("Cancel image picking", "Task Cancelled")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@EditProfileActivity)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
                finish()
            }
        })

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
        val alamat = myPreferences.getValue(Constants.USER_ALAMAT).toString()
        val telp = myPreferences.getValue(Constants.USER_TELP).toString()
        val foto = myPreferences.getValue(Constants.FOTO_PATH).toString()

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            tvValueEmailEdit.setText(email)
            tvValueNameEdit.setText(nama)
            tvValueAddressEdit.setText(alamat)
            tvValuePhoneEdit.setText(telp)

            Glide.with(this@EditProfileActivity)
                .load(foto)
                .apply(RequestOptions()).override(150)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(imgProfile)

            btnChangeImg.setOnClickListener {
                ImagePicker.with(this@EditProfileActivity).apply {
                    cropSquare()
                    compress(1024)
                    maxResultSize(1080, 1080)
                    galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))

                    createIntent {
                        startForProfileImageResult.launch(it)
                    }
                }
            }

            btnSave.setOnClickListener {
                if (validate()) {
                    btnSave.startAnimation()
                    val editEmail = tvValueEmailEdit.text.toString()
                    val editName = tvValueNameEdit.text.toString()
                    val editAddress = tvValueAddressEdit.text.toString()
                    val editPhone = tvValuePhoneEdit.text.toString()
                    // TODO: insert foto to API
                    var editFoto: MultipartBody.Part? = null
                    photoUri?.let {
                        val file = FileUtils.getFile(this@EditProfileActivity, it)
                        val requestBodyPhoto = file?.asRequestBody(contentResolver.getType(it).toString().toMediaTypeOrNull())
                        editFoto = requestBodyPhoto?.let { it1 -> MultipartBody.Part.createFormData("foto", file.name, it1) }
                    }

                    editProfile(idadmin, editEmail, editName, editAddress, editPhone, tokenAuth)
                }
            }
        }
    }

    private fun validate(): Boolean {
        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        binding.apply {
            return when {
                tvValueEmailEdit.text.toString() == "" -> {
                    tvValueEmailEdit.error = getString(R.string.email_cant_empty)
                    tvValueEmailEdit.requestFocus()
                    false
                }

                !tvValueEmailEdit.text.toString().isValidEmail() -> {
                    tvValueEmailEdit.error = getString(R.string.email_format_error)
                    tvValueEmailEdit.requestFocus()
                    false
                }

                tvValueNameEdit.text.toString() == "" -> {
                    tvValueNameEdit.error = "Nama tidak boleh kosong"
                    tvValueNameEdit.requestFocus()
                    false
                }

                tvValueAddressEdit.text.toString() == "" -> {
                    tvValueAddressEdit.error = "Alamat tidak boleh kosong"
                    tvValueAddressEdit.requestFocus()
                    false
                }

                tvValuePhoneEdit.text.toString() == "" -> {
                    tvValuePhoneEdit.error = "Nomor HP tidak boleh kosong"
                    tvValuePhoneEdit.requestFocus()
                    false
                }

                else -> true
            }
        }
    }

    private fun editProfile(idadmin: String, email: String, nama: String, alamat: String, telp: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.editProfile(idadmin, email, nama, alamat, telp, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.USER_EMAIL, email)
                        myPreferences.setValue(Constants.USER_NAMA, nama)
                        myPreferences.setValue(Constants.USER_ALAMAT, alamat)
                        myPreferences.setValue(Constants.USER_TELP, telp)
                        Toasty.success(this@EditProfileActivity, "Profil berhasil dirubah", Toasty.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@EditProfileActivity, "editProfile | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.btnSave.endAnimation()
                ErrorHandler().responseHandler(
                    this@EditProfileActivity, "editProfile | onFailure", t.message.toString()
                )
            }
        })
    }
}