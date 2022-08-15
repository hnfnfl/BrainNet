package com.jaylangkung.indirisma.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityEditProfileBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var myPreferences: MySharedPreferences
//    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@EditProfileActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
        val alamat = myPreferences.getValue(Constants.USER_ALAMAT).toString()
        val telp = myPreferences.getValue(Constants.USER_TELP).toString()

        binding.apply {
            tvValueEmailEdit.setText(email)
            tvValueNameEdit.setText(nama)
            tvValueAddressEdit.setText(alamat)
            tvValuePhoneEdit.setText(telp)

            btnBack.setOnClickListener { onBackPressed() }

            btnSave.setOnClickListener {
                if (validate()) {
                    btnSave.startAnimation()
                    val editEmail = tvValueEmailEdit.text.toString()
                    val editName = tvValueNameEdit.text.toString()
                    val editAddress = tvValueAddressEdit.text.toString()
                    val editPhone = tvValuePhoneEdit.text.toString()
                    editProfile(idadmin, editEmail, editName, editAddress, editPhone, tokenAuth)
                }
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
        finish()
    }

    private fun validate(): Boolean {
        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        when {
            binding.tvValueEmailEdit.text.toString() == "" -> {
                binding.tvValueEmailEdit.error = getString(R.string.email_cant_empty)
                binding.tvValueEmailEdit.requestFocus()
                return false
            }
            !binding.tvValueEmailEdit.text.toString().isValidEmail() -> {
                binding.tvValueEmailEdit.error = getString(R.string.email_format_error)
                binding.tvValueEmailEdit.requestFocus()
                return false
            }
            binding.tvValueNameEdit.text.toString() == "" -> {
                binding.tvValueNameEdit.error = "Nama tidak boleh kosong"
                binding.tvValueNameEdit.requestFocus()
                return false
            }
            binding.tvValueAddressEdit.text.toString() == "" -> {
                binding.tvValueAddressEdit.error = "Alamat tidak boleh kosong"
                binding.tvValueAddressEdit.requestFocus()
                return false
            }
            binding.tvValuePhoneEdit.text.toString() == "" -> {
                binding.tvValuePhoneEdit.error = "Nomor HP tidak boleh kosong"
                binding.tvValuePhoneEdit.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun editProfile(idadmin: String, email: String, nama: String, alamat: String, telp: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.editProfile(idadmin, email, nama, alamat, telp, tokenAuth, "true").enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.USER_EMAIL, email)
                        myPreferences.setValue(Constants.USER_NAMA, nama)
                        myPreferences.setValue(Constants.USER_ALAMAT, alamat)
                        myPreferences.setValue(Constants.USER_TELP, telp)
                        Toasty.success(this@EditProfileActivity, "Profil berhasil dirubah", Toasty.LENGTH_LONG).show()
                        startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
                        finish()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@EditProfileActivity,
                        "editProfile | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.btnSave.endAnimation()
                ErrorHandler().responseHandler(
                    this@EditProfileActivity,
                    "editProfile | onFailure", t.message.toString()
                )
            }
        })
    }
}